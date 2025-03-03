.intel_syntax noprefix
.globl main
.section .rodata
.text
main:
	push rbp
	push rbx
	mov rbp, rsp
	mov al, 1
	cmp al, 1
	je .L0
	jmp .L1
.L0:
.L1:
	mov rax, 0
	pop rbx
	pop rbp
	ret
