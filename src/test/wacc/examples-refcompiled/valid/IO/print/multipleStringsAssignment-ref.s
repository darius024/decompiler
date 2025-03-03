.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 2
.L.str0:
	.asciz "Hi"
# length of .L.str1
	.int 5
.L.str1:
	.asciz "Hello"
# length of .L.str2
	.int 6
.L.str2:
	.asciz "s1 is "
# length of .L.str3
	.int 6
.L.str3:
	.asciz "s2 is "
# length of .L.str4
	.int 29
.L.str4:
	.asciz "They are not the same string."
# length of .L.str5
	.int 25
.L.str5:
	.asciz "They are the same string."
# length of .L.str6
	.int 16
.L.str6:
	.asciz "Now make s1 = s2"
.text
main:
	push rbp
	# push {rbx, r12, r13}
	sub rsp, 24
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov qword ptr [rsp + 16], r13
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
	mov r13, rax
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov rax, r13
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	cmp r12, r13
	je .L0
	lea rax, [rip + .L.str4]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	jmp .L1
.L0:
	lea rax, [rip + .L.str5]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
.L1:
	lea rax, [rip + .L.str6]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	mov rax, r13
	mov r12, rax
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov rax, r13
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	cmp r12, r13
	je .L2
	lea rax, [rip + .L.str4]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	jmp .L3
.L2:
	lea rax, [rip + .L.str5]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
.L3:
	mov rax, 0
	# pop/peek {rbx, r12, r13}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	mov r13, qword ptr [rsp + 16]
	add rsp, 24
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
