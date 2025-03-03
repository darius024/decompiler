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
	add eax, 3
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 4
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 5
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 6
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 7
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 8
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 9
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 10
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 11
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 12
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 13
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 14
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 15
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 16
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 17
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r12d, eax
	mov eax, -1
	sub eax, 2
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 3
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 4
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 5
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 6
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 7
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 8
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 9
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 10
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 11
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 12
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 13
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 14
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 15
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 16
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	sub eax, 17
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
	mov eax, 1
	imul eax, eax, 2
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 3
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 4
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 5
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 6
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 7
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 8
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 9
	jo _errOverflow
	push rax
	pop rax
	imul eax, eax, 10
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r14d, eax
	mov eax, 10
	mov r15d, eax
	mov eax, r12d
	add eax, r13d
	jo _errOverflow
	push rax
	cmp r15d, 0
	je _errDivZero
	mov eax, r14d
	# sign extend EAX into EDX
	cdq
	idiv r15d
	mov eax, eax
	mov eax, eax
	push rax
	pop rbx
	pop rax
	mov eax, eax
	add eax, ebx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	mov eax, r12d
	add eax, r13d
	jo _errOverflow
	push rax
	cmp r15d, 0
	je _errDivZero
	mov eax, r14d
	# sign extend EAX into EDX
	cdq
	idiv r15d
	mov eax, eax
	mov eax, eax
	push rax
	pop rbx
	pop rax
	mov eax, eax
	add eax, ebx
	jo _errOverflow
	push rax
	mov ebx, 256
	cmp ebx, 0
	je _errDivZero
	pop rax
	mov eax, eax
	# sign extend EAX into EDX
	cdq
	idiv ebx
	mov eax, edx
	mov eax, eax
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	mov eax, r12d
	add eax, r13d
	jo _errOverflow
	push rax
	cmp r15d, 0
	je _errDivZero
	mov eax, r14d
	# sign extend EAX into EDX
	cdq
	idiv r15d
	mov eax, eax
	mov eax, eax
	push rax
	pop rbx
	pop rax
	mov eax, eax
	add eax, ebx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
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
# length of .L._errDivZero_str0
	.int 40
.L._errDivZero_str0:
	.asciz "fatal error: division or modulo by zero\n"
.text
_errDivZero:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errDivZero_str0]
	call _prints
	mov dil, -1
	call exit@plt

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
