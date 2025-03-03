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
	mov eax, 2
	add eax, 3
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 2
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	add eax, 1
	jo _errOverflow
	push rax
	mov eax, 1
	add eax, 2
	jo _errOverflow
	push rax
	mov ebx, 6
	cmp ebx, 0
	je _errDivZero
	mov eax, 4
	# sign extend EAX into EDX
	cdq
	idiv ebx
	mov eax, eax
	mov eax, eax
	push rax
	pop rbx
	mov eax, 3
	sub eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	pop rax
	mov eax, eax
	imul eax, ebx
	jo _errOverflow
	push rax
	mov eax, 18
	sub eax, 17
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 2
	mov eax, eax
	imul eax, ebx
	jo _errOverflow
	push rax
	mov eax, 3
	imul eax, eax, 4
	jo _errOverflow
	push rax
	mov ebx, 4
	cmp ebx, 0
	je _errDivZero
	pop rax
	mov eax, eax
	# sign extend EAX into EDX
	cdq
	idiv ebx
	mov eax, eax
	mov eax, eax
	push rax
	pop rax
	mov eax, eax
	add eax, 6
	jo _errOverflow
	push rax
	pop rbx
	pop rax
	mov eax, eax
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	cmp ebx, 0
	je _errDivZero
	pop rax
	mov eax, eax
	# sign extend EAX into EDX
	cdq
	idiv ebx
	mov eax, eax
	mov eax, eax
	push rax
	pop rbx
	pop rax
	mov eax, eax
	sub eax, ebx
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r12d, eax
	mov eax, r12d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
	mov rax, 0
	# pop/peek {rbx, r12}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	add rsp, 16
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
