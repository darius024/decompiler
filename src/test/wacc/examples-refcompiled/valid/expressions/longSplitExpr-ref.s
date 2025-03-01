.intel_syntax noprefix
.globl main
.section .rodata
.text
main:
	push rbp
	# push {rbx, r12, r13, r14, r15}
	sub rsp, 40
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov qword ptr [rsp + 16], r13
	mov qword ptr [rsp + 24], r14
	mov qword ptr [rsp + 32], r15
	mov rbp, rsp
	mov eax, 1
	add eax, 2
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r12d, eax
	mov eax, 3
	add eax, 4
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
	mov eax, 5
	add eax, 6
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r14d, eax
	mov eax, 7
	add eax, 8
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r15d, eax
	mov eax, 9
	add eax, 10
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov ecx, eax
	mov eax, 11
	add eax, 12
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edx, eax
	mov eax, 13
	add eax, 14
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov esi, eax
	mov eax, 15
	add eax, 16
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	mov eax, 17
	mov r8d, eax
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r12d
	add eax, r13d
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, r14d
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, r15d
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, ecx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, edx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, esi
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, edi
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, r8d
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov rax, 0
	# pop/peek {rbx, r12, r13, r14, r15}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	mov r13, qword ptr [rsp + 16]
	mov r14, qword ptr [rsp + 24]
	mov r15, qword ptr [rsp + 32]
	add rsp, 40
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

_exit:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	call exit@plt
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
