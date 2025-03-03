.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 10
.L.str0:
	.asciz "looping..."
# length of .L.str1
	.int 11
.L.str1:
	.asciz "end of loop"
.text
main:
	push rbp
	push rbx
	mov rbp, rsp
	jmp .L0
.L1:
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
.L0:
	mov al, 0
	cmp al, 1
	je .L1
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	mov rax, 0
	pop rbx
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
