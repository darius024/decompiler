.intel_syntax noprefix
.globl main
.section .rodata
.text
main:
	push rbp
	# push {rbx, r12}
	sub rsp, 16
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov rbp, rsp
	mov eax, 1
	mov edi, eax
	mov eax, 2
	mov esi, eax
	mov eax, 3
	mov edx, eax
	mov eax, 4
	mov ecx, eax
	mov eax, 5
	mov r8d, eax
	mov eax, 6
	mov r9d, eax
	call wacc_f
	mov r11d, eax
	mov eax, r11d
	mov r12d, eax
	mov eax, r12d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	mov rax, 0
	# pop/peek {rbx, r12}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret

wacc_f:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 7
	mov eax, 1
	mov r12d, eax
	mov eax, 2
	mov r13d, eax
	mov eax, 3
	mov r14d, eax
	mov eax, 4
	mov r15d, eax
	mov al, 53
	mov byte ptr [rbp - 7], al
	mov al, 54
	mov byte ptr [rbp - 6], al
	mov al, 55
	mov byte ptr [rbp - 5], al
	mov eax, 8
	mov dword ptr [rbp - 4], eax
	sub rsp, 6
	mov al, 97
	mov byte ptr [rbp - 13], al
	mov al, 98
	mov byte ptr [rbp - 12], al
	mov eax, 5
	mov dword ptr [rbp - 11], eax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, dword ptr [rbp - 11]
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	add rsp, 6
	mov eax, dword ptr [rbp - 4]
	mov eax, eax
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14, r15}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	mov r15, qword ptr [rsp + 24]
	add rsp, 32
	pop rbp
	ret
	add rsp, 7
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

.section .rodata
# length of .L._printi_str0
	.int 2
.L._printi_str0:
	.asciz "%d"
.text
_printi:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov esi, edi
	lea rdi, [rip + .L._printi_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._println_str0
	.int 0
.L._println_str0:
	.asciz ""
.text
_println:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._println_str0]
	call puts@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret
