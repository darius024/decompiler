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
	mov eax, 16
	add eax, 17
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 15
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 14
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 13
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 12
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 11
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 10
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 9
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 8
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 7
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 6
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 5
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 4
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 3
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 2
	add eax, ebx
	jo _errOverflow
	push rax
	pop rbx
	mov eax, 1
	add eax, ebx
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
