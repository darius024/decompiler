.intel_syntax noprefix
.globl main
.section .rodata
.text
main:
	push rbp
	push rbx
	mov rbp, rsp
	jmp .L0
.L1:
.L0:
	mov al, 0
	cmp al, 1
	je .L1
	mov rax, 0
	pop rbx
	pop rbp
	ret
