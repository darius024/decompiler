.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 3
.L.str0:
	.asciz "foo"
# length of .L.str1
	.int 3
.L.str1:
	.asciz "bar"
.text
main:
	push rbp
	# push {rbx, r12}
	sub rsp, 16
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov rbp, rsp
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	mov rax, 0
	# pop/peek {rbx, r12}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
