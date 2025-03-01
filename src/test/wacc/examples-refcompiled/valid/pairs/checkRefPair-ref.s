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
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, 10
	mov qword ptr [r11], rax
	mov al, 97
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r12, rax
	mov rax, r12
	mov r13, rax
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	cmp r12, r13
	sete al
	push ax
	pop ax
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov eax, eax
	mov r14d, eax
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13]
	mov eax, eax
	mov r15d, eax
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r14d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r15d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	cmp r14d, r15d
	sete al
	push ax
	pop ax
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov al, al
	mov cl, al
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13 + 8]
	mov al, al
	mov dl, al
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, cl
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, dl
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdx}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	cmp cl, dl
	sete al
	push ax
	pop ax
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	call _println
	# pop/peek {rcx, rdx}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	add rsp, 16
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
# length of .L._printc_str0
	.int 2
.L._printc_str0:
	.asciz "%c"
.text
_printc:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov sil, dil
	lea rdi, [rip + .L._printc_str0]
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
# length of .L._printb_str0
	.int 5
.L._printb_str0:
	.asciz "false"
# length of .L._printb_str1
	.int 4
.L._printb_str1:
	.asciz "true"
# length of .L._printb_str2
	.int 4
.L._printb_str2:
	.asciz "%.*s"
.text
_printb:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	cmp dil, 0
	jne .L_printb0
	lea rdx, [rip + .L._printb_str0]
	jmp .L_printb1
.L_printb0:
	lea rdx, [rip + .L._printb_str1]
.L_printb1:
	mov esi, dword ptr [rdx - 4]
	lea rdi, [rip + .L._printb_str2]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._printp_str0
	.int 2
.L._printp_str0:
	.asciz "%p"
.text
_printp:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov rsi, rdi
	lea rdi, [rip + .L._printp_str0]
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

_malloc:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	call malloc@plt
	cmp rax, 0
	je _errOutOfMemory
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._errNull_str0
	.int 45
.L._errNull_str0:
	.asciz "fatal error: null pair dereferenced or freed\n"
.text
_errNull:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errNull_str0]
	call _prints
	mov dil, -1
	call exit@plt

.section .rodata
# length of .L._errOutOfMemory_str0
	.int 27
.L._errOutOfMemory_str0:
	.asciz "fatal error: out of memory\n"
.text
_errOutOfMemory:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errOutOfMemory_str0]
	call _prints
	mov dil, -1
	call exit@plt
