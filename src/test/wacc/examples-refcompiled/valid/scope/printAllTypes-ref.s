.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 2
.L.str0:
	.asciz ", "
# length of .L.str1
	.int 16
.L.str1:
	.asciz "this is a string"
# length of .L.str2
	.int 5
.L.str2:
	.asciz "array"
# length of .L.str3
	.int 2
.L.str3:
	.asciz "of"
# length of .L.str4
	.int 7
.L.str4:
	.asciz "strings"
# length of .L.str5
	.int 3
.L.str5:
	.asciz "( ["
# length of .L.str6
	.int 5
.L.str6:
	.asciz "] , ["
# length of .L.str7
	.int 3
.L.str7:
	.asciz "] )"
# length of .L.str8
	.int 2
.L.str8:
	.asciz "[ "
# length of .L.str9
	.int 4
.L.str9:
	.asciz " = ("
# length of .L.str10
	.int 3
.L.str10:
	.asciz "), "
# length of .L.str11
	.int 3
.L.str11:
	.asciz ") ]"
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
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	mov eax, 5
	mov r13d, eax
	mov al, 120
	mov r14b, al
	mov al, 1
	mov r15b, al
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rcx, rax
	# 3 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 3
	mov dword ptr [r11 - 4], eax
	mov eax, 1
	mov dword ptr [r11], eax
	mov eax, 2
	mov dword ptr [r11 + 4], eax
	mov eax, 3
	mov dword ptr [r11 + 8], eax
	mov rax, r11
	mov rdx, rax
	# 3 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 7
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 3
	mov dword ptr [r11 - 4], eax
	mov al, 120
	mov byte ptr [r11], al
	mov al, 121
	mov byte ptr [r11 + 1], al
	mov al, 122
	mov byte ptr [r11 + 2], al
	mov rax, r11
	mov r9, rax
	sub rsp, 8
	# 3 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 7
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 3
	mov dword ptr [r11 - 4], eax
	mov al, 1
	mov byte ptr [r11], al
	mov al, 0
	mov byte ptr [r11 + 1], al
	mov al, 1
	mov byte ptr [r11 + 2], al
	mov rax, r11
	mov qword ptr [rbp - 8], rax
	sub rsp, 32
	# 3 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 28
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 3
	mov dword ptr [r11 - 4], eax
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov qword ptr [r11], rax
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov qword ptr [r11 + 8], rax
	lea rax, [rip + .L.str4]
	push rax
	pop rax
	mov rax, rax
	mov qword ptr [r11 + 16], rax
	mov rax, r11
	mov qword ptr [rbp - 40], rax
	sub rsp, 16
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, 1
	mov qword ptr [r11], rax
	mov eax, 2
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov qword ptr [rbp - 56], rax
	sub rsp, 44
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, 97
	mov qword ptr [r11], rax
	mov al, 1
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov qword ptr [rbp - 100], rax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, 98
	mov qword ptr [r11], rax
	mov al, 0
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov qword ptr [rbp - 92], rax
	# 2 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 20
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 2
	mov dword ptr [r11 - 4], eax
	mov rax, qword ptr [rbp - 100]
	mov rax, rax
	mov qword ptr [r11], rax
	mov rax, qword ptr [rbp - 92]
	mov rax, rax
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov qword ptr [rbp - 84], rax
	sub rsp, 40
	# 3 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 3
	mov dword ptr [r11 - 4], eax
	mov eax, 1
	mov dword ptr [r11], eax
	mov eax, 2
	mov dword ptr [r11 + 4], eax
	mov eax, 3
	mov dword ptr [r11 + 8], eax
	mov rax, r11
	mov qword ptr [rbp - 140], rax
	# 3 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 7
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 3
	mov dword ptr [r11 - 4], eax
	mov al, 97
	mov byte ptr [r11], al
	mov al, 98
	mov byte ptr [r11 + 1], al
	mov al, 99
	mov byte ptr [r11 + 2], al
	mov rax, r11
	mov qword ptr [rbp - 132], rax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov rax, qword ptr [rbp - 140]
	mov rax, rax
	mov qword ptr [r11], rax
	mov rax, qword ptr [rbp - 132]
	mov rax, rax
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov qword ptr [rbp - 124], rax
	mov rax, qword ptr [rbp - 124]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax]
	mov rax, rax
	mov qword ptr [rbp - 116], rax
	mov rax, qword ptr [rbp - 124]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax + 8]
	mov rax, rax
	mov qword ptr [rbp - 108], rax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str5]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 0
	mov rax, qword ptr [rbp - 116]
	push r9
	mov r9, rax
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 1
	mov rax, qword ptr [rbp - 116]
	push r9
	mov r9, rax
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 2
	mov rax, qword ptr [rbp - 116]
	push r9
	mov r9, rax
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str6]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 0
	mov rax, qword ptr [rbp - 108]
	push r9
	mov r9, rax
	call _arrLoad1
	mov al, r9b
	pop r9
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 1
	mov rax, qword ptr [rbp - 108]
	push r9
	mov r9, rax
	call _arrLoad1
	mov al, r9b
	pop r9
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 2
	mov rax, qword ptr [rbp - 108]
	push r9
	mov r9, rax
	call _arrLoad1
	mov al, r9b
	pop r9
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str7]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	add rsp, 40
	mov r10d, 0
	mov rax, qword ptr [rbp - 84]
	push r9
	mov r9, rax
	call _arrLoad8
	mov rax, r9
	pop r9
	mov rax, rax
	mov qword ptr [rbp - 76], rax
	mov rax, qword ptr [rbp - 76]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax]
	mov al, al
	mov byte ptr [rbp - 68], al
	mov rax, qword ptr [rbp - 76]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax + 8]
	mov al, al
	mov byte ptr [rbp - 67], al
	mov r10d, 1
	mov rax, qword ptr [rbp - 84]
	push r9
	mov r9, rax
	call _arrLoad8
	mov rax, r9
	pop r9
	mov rax, rax
	mov qword ptr [rbp - 66], rax
	mov rax, qword ptr [rbp - 66]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax]
	mov al, al
	mov byte ptr [rbp - 58], al
	mov rax, qword ptr [rbp - 66]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax + 8]
	mov al, al
	mov byte ptr [rbp - 57], al
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str8]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, qword ptr [rbp - 76]
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str9]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, byte ptr [rbp - 68]
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, byte ptr [rbp - 67]
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str10]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, qword ptr [rbp - 66]
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str9]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, byte ptr [rbp - 58]
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, byte ptr [rbp - 57]
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str11]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	add rsp, 44
	mov rax, qword ptr [rbp - 56]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax]
	mov eax, eax
	mov dword ptr [rbp - 48], eax
	mov rax, qword ptr [rbp - 56]
	cmp rax, 0
	je _errNull
	mov rax, qword ptr [rax + 8]
	mov eax, eax
	mov dword ptr [rbp - 44], eax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, dword ptr [rbp - 48]
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, dword ptr [rbp - 44]
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	add rsp, 16
	mov r10d, 0
	mov rax, qword ptr [rbp - 40]
	push r9
	mov r9, rax
	call _arrLoad8
	mov rax, r9
	pop r9
	mov rax, rax
	mov qword ptr [rbp - 32], rax
	mov r10d, 1
	mov rax, qword ptr [rbp - 40]
	push r9
	mov r9, rax
	call _arrLoad8
	mov rax, r9
	pop r9
	mov rax, rax
	mov qword ptr [rbp - 24], rax
	mov r10d, 2
	mov rax, qword ptr [rbp - 40]
	push r9
	mov r9, rax
	call _arrLoad8
	mov rax, r9
	pop r9
	mov rax, rax
	mov qword ptr [rbp - 16], rax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, qword ptr [rbp - 32]
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, qword ptr [rbp - 24]
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, qword ptr [rbp - 16]
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	add rsp, 32
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 0
	mov rax, qword ptr [rbp - 8]
	push r9
	mov r9, rax
	call _arrLoad1
	mov al, r9b
	pop r9
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 1
	mov rax, qword ptr [rbp - 8]
	push r9
	mov r9, rax
	call _arrLoad1
	mov al, r9b
	pop r9
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, 2
	mov rax, qword ptr [rbp - 8]
	push r9
	mov r9, rax
	call _arrLoad1
	mov al, r9b
	pop r9
	mov al, al
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	add rsp, 8
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r9
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov r10d, 0
	push r9
	mov r9, rdx
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov esi, eax
	mov r10d, 1
	push r9
	mov r9, rdx
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov edi, eax
	mov r10d, 2
	push r9
	mov r9, rdx
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov r8d, eax
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, esi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r8d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rcx
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, r15b
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, r14b
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r13d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
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

_arrLoad8:
	# Special calling convention: array ptr passed in R9, index in R10, and return into R9
	push rbx
	# `test r, r` is equivalent to `cmp r, 0`
	test r10d, r10d
	cmovl rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jl _errOutOfBounds
	mov ebx, dword ptr [r9 - 4]
	cmp r10d, ebx
	cmovge rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jge _errOutOfBounds
	mov r9, qword ptr [r9 + 8*r10]
	pop rbx
	ret

_arrLoad4:
	# Special calling convention: array ptr passed in R9, index in R10, and return into R9
	push rbx
	# `test r, r` is equivalent to `cmp r, 0`
	test r10d, r10d
	cmovl rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jl _errOutOfBounds
	mov ebx, dword ptr [r9 - 4]
	cmp r10d, ebx
	cmovge rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jge _errOutOfBounds
	mov r9d, dword ptr [r9 + 4*r10]
	pop rbx
	ret

_arrLoad1:
	# Special calling convention: array ptr passed in R9, index in R10, and return into R9
	push rbx
	# `test r, r` is equivalent to `cmp r, 0`
	test r10d, r10d
	cmovl rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jl _errOutOfBounds
	mov ebx, dword ptr [r9 - 4]
	cmp r10d, ebx
	cmovge rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jge _errOutOfBounds
	mov r9b, byte ptr [r9 + r10]
	pop rbx
	ret

.section .rodata
# length of .L._errOutOfBounds_str0
	.int 42
.L._errOutOfBounds_str0:
	.asciz "fatal error: array index %d out of bounds\n"
.text
_errOutOfBounds:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errOutOfBounds_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov dil, -1
	call exit@plt

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
