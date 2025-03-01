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
	sub rsp, 8
	mov eax, 0
	mov edi, eax
	mov eax, 0
	mov esi, eax
	mov eax, 1
	mov edx, eax
	mov eax, 4
	mov ecx, eax
	mov eax, 2
	mov r8d, eax
	mov eax, 3
	mov r9d, eax
	mov eax, 7
	mov dword ptr [rsp + 4], eax
	mov eax, 4
	mov dword ptr [rsp], eax
	call wacc_f
	mov r11d, eax
	add rsp, 8
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
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	mov eax, edx
	add eax, ecx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r12d, eax
	mov eax, r8d
	imul eax, r9d
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
	mov ebx, dword ptr [rbp + 40]
	mov eax, dword ptr [rbp + 44]
	mov eax, eax
	sub eax, ebx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r14d, eax
	mov eax, r13d
	imul eax, r14d
	jo _errOverflow
	push rax
	pop rbx
	mov eax, r12d
	add eax, ebx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	add rsp, 24
	pop rbp
	ret
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
# length of .L._prints_str0
	.int 4
.L._prints_str0:
	.asciz "%.*s"
.text
_prints:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov rdx, rdi
	mov esi, dword ptr [rdi - 4]
	lea rdi, [rip + .L._prints_str0]
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

.section .rodata
# length of .L._errOverflow_str0
	.int 52
.L._errOverflow_str0:
	.asciz "fatal error: integer overflow or underflow occurred\n"
.text
_errOverflow:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errOverflow_str0]
	call _prints
	mov dil, -1
	call exit@plt
