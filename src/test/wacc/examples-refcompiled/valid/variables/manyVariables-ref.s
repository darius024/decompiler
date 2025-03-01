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
	sub rsp, 988
	mov eax, 0
	mov r12d, eax
	mov eax, 1
	mov r13d, eax
	mov eax, 2
	mov r14d, eax
	mov eax, 3
	mov r15d, eax
	mov eax, 4
	mov ecx, eax
	mov eax, 5
	mov edx, eax
	mov eax, 6
	mov esi, eax
	mov eax, 7
	mov edi, eax
	mov eax, 8
	mov r8d, eax
	mov eax, 9
	mov r9d, eax
	mov eax, 10
	mov dword ptr [rbp - 988], eax
	mov eax, 11
	mov dword ptr [rbp - 984], eax
	mov eax, 12
	mov dword ptr [rbp - 980], eax
	mov eax, 13
	mov dword ptr [rbp - 976], eax
	mov eax, 14
	mov dword ptr [rbp - 972], eax
	mov eax, 15
	mov dword ptr [rbp - 968], eax
	mov eax, 16
	mov dword ptr [rbp - 964], eax
	mov eax, 17
	mov dword ptr [rbp - 960], eax
	mov eax, 18
	mov dword ptr [rbp - 956], eax
	mov eax, 19
	mov dword ptr [rbp - 952], eax
	mov eax, 20
	mov dword ptr [rbp - 948], eax
	mov eax, 21
	mov dword ptr [rbp - 944], eax
	mov eax, 22
	mov dword ptr [rbp - 940], eax
	mov eax, 23
	mov dword ptr [rbp - 936], eax
	mov eax, 24
	mov dword ptr [rbp - 932], eax
	mov eax, 25
	mov dword ptr [rbp - 928], eax
	mov eax, 26
	mov dword ptr [rbp - 924], eax
	mov eax, 27
	mov dword ptr [rbp - 920], eax
	mov eax, 28
	mov dword ptr [rbp - 916], eax
	mov eax, 29
	mov dword ptr [rbp - 912], eax
	mov eax, 30
	mov dword ptr [rbp - 908], eax
	mov eax, 31
	mov dword ptr [rbp - 904], eax
	mov eax, 32
	mov dword ptr [rbp - 900], eax
	mov eax, 33
	mov dword ptr [rbp - 896], eax
	mov eax, 34
	mov dword ptr [rbp - 892], eax
	mov eax, 35
	mov dword ptr [rbp - 888], eax
	mov eax, 36
	mov dword ptr [rbp - 884], eax
	mov eax, 37
	mov dword ptr [rbp - 880], eax
	mov eax, 38
	mov dword ptr [rbp - 876], eax
	mov eax, 39
	mov dword ptr [rbp - 872], eax
	mov eax, 40
	mov dword ptr [rbp - 868], eax
	mov eax, 41
	mov dword ptr [rbp - 864], eax
	mov eax, 42
	mov dword ptr [rbp - 860], eax
	mov eax, 43
	mov dword ptr [rbp - 856], eax
	mov eax, 44
	mov dword ptr [rbp - 852], eax
	mov eax, 45
	mov dword ptr [rbp - 848], eax
	mov eax, 46
	mov dword ptr [rbp - 844], eax
	mov eax, 47
	mov dword ptr [rbp - 840], eax
	mov eax, 48
	mov dword ptr [rbp - 836], eax
	mov eax, 49
	mov dword ptr [rbp - 832], eax
	mov eax, 50
	mov dword ptr [rbp - 828], eax
	mov eax, 51
	mov dword ptr [rbp - 824], eax
	mov eax, 52
	mov dword ptr [rbp - 820], eax
	mov eax, 53
	mov dword ptr [rbp - 816], eax
	mov eax, 54
	mov dword ptr [rbp - 812], eax
	mov eax, 55
	mov dword ptr [rbp - 808], eax
	mov eax, 56
	mov dword ptr [rbp - 804], eax
	mov eax, 57
	mov dword ptr [rbp - 800], eax
	mov eax, 58
	mov dword ptr [rbp - 796], eax
	mov eax, 59
	mov dword ptr [rbp - 792], eax
	mov eax, 60
	mov dword ptr [rbp - 788], eax
	mov eax, 61
	mov dword ptr [rbp - 784], eax
	mov eax, 62
	mov dword ptr [rbp - 780], eax
	mov eax, 63
	mov dword ptr [rbp - 776], eax
	mov eax, 64
	mov dword ptr [rbp - 772], eax
	mov eax, 65
	mov dword ptr [rbp - 768], eax
	mov eax, 66
	mov dword ptr [rbp - 764], eax
	mov eax, 67
	mov dword ptr [rbp - 760], eax
	mov eax, 68
	mov dword ptr [rbp - 756], eax
	mov eax, 69
	mov dword ptr [rbp - 752], eax
	mov eax, 70
	mov dword ptr [rbp - 748], eax
	mov eax, 71
	mov dword ptr [rbp - 744], eax
	mov eax, 72
	mov dword ptr [rbp - 740], eax
	mov eax, 73
	mov dword ptr [rbp - 736], eax
	mov eax, 74
	mov dword ptr [rbp - 732], eax
	mov eax, 75
	mov dword ptr [rbp - 728], eax
	mov eax, 76
	mov dword ptr [rbp - 724], eax
	mov eax, 77
	mov dword ptr [rbp - 720], eax
	mov eax, 78
	mov dword ptr [rbp - 716], eax
	mov eax, 79
	mov dword ptr [rbp - 712], eax
	mov eax, 80
	mov dword ptr [rbp - 708], eax
	mov eax, 81
	mov dword ptr [rbp - 704], eax
	mov eax, 82
	mov dword ptr [rbp - 700], eax
	mov eax, 83
	mov dword ptr [rbp - 696], eax
	mov eax, 84
	mov dword ptr [rbp - 692], eax
	mov eax, 85
	mov dword ptr [rbp - 688], eax
	mov eax, 86
	mov dword ptr [rbp - 684], eax
	mov eax, 87
	mov dword ptr [rbp - 680], eax
	mov eax, 88
	mov dword ptr [rbp - 676], eax
	mov eax, 89
	mov dword ptr [rbp - 672], eax
	mov eax, 90
	mov dword ptr [rbp - 668], eax
	mov eax, 91
	mov dword ptr [rbp - 664], eax
	mov eax, 92
	mov dword ptr [rbp - 660], eax
	mov eax, 93
	mov dword ptr [rbp - 656], eax
	mov eax, 94
	mov dword ptr [rbp - 652], eax
	mov eax, 95
	mov dword ptr [rbp - 648], eax
	mov eax, 96
	mov dword ptr [rbp - 644], eax
	mov eax, 97
	mov dword ptr [rbp - 640], eax
	mov eax, 98
	mov dword ptr [rbp - 636], eax
	mov eax, 99
	mov dword ptr [rbp - 632], eax
	mov eax, 100
	mov dword ptr [rbp - 628], eax
	mov eax, 101
	mov dword ptr [rbp - 624], eax
	mov eax, 102
	mov dword ptr [rbp - 620], eax
	mov eax, 103
	mov dword ptr [rbp - 616], eax
	mov eax, 104
	mov dword ptr [rbp - 612], eax
	mov eax, 105
	mov dword ptr [rbp - 608], eax
	mov eax, 106
	mov dword ptr [rbp - 604], eax
	mov eax, 107
	mov dword ptr [rbp - 600], eax
	mov eax, 108
	mov dword ptr [rbp - 596], eax
	mov eax, 109
	mov dword ptr [rbp - 592], eax
	mov eax, 110
	mov dword ptr [rbp - 588], eax
	mov eax, 111
	mov dword ptr [rbp - 584], eax
	mov eax, 112
	mov dword ptr [rbp - 580], eax
	mov eax, 113
	mov dword ptr [rbp - 576], eax
	mov eax, 114
	mov dword ptr [rbp - 572], eax
	mov eax, 115
	mov dword ptr [rbp - 568], eax
	mov eax, 116
	mov dword ptr [rbp - 564], eax
	mov eax, 117
	mov dword ptr [rbp - 560], eax
	mov eax, 118
	mov dword ptr [rbp - 556], eax
	mov eax, 119
	mov dword ptr [rbp - 552], eax
	mov eax, 120
	mov dword ptr [rbp - 548], eax
	mov eax, 121
	mov dword ptr [rbp - 544], eax
	mov eax, 122
	mov dword ptr [rbp - 540], eax
	mov eax, 123
	mov dword ptr [rbp - 536], eax
	mov eax, 124
	mov dword ptr [rbp - 532], eax
	mov eax, 125
	mov dword ptr [rbp - 528], eax
	mov eax, 126
	mov dword ptr [rbp - 524], eax
	mov eax, 127
	mov dword ptr [rbp - 520], eax
	mov eax, 128
	mov dword ptr [rbp - 516], eax
	mov eax, 129
	mov dword ptr [rbp - 512], eax
	mov eax, 130
	mov dword ptr [rbp - 508], eax
	mov eax, 131
	mov dword ptr [rbp - 504], eax
	mov eax, 132
	mov dword ptr [rbp - 500], eax
	mov eax, 133
	mov dword ptr [rbp - 496], eax
	mov eax, 134
	mov dword ptr [rbp - 492], eax
	mov eax, 135
	mov dword ptr [rbp - 488], eax
	mov eax, 136
	mov dword ptr [rbp - 484], eax
	mov eax, 137
	mov dword ptr [rbp - 480], eax
	mov eax, 138
	mov dword ptr [rbp - 476], eax
	mov eax, 139
	mov dword ptr [rbp - 472], eax
	mov eax, 140
	mov dword ptr [rbp - 468], eax
	mov eax, 141
	mov dword ptr [rbp - 464], eax
	mov eax, 142
	mov dword ptr [rbp - 460], eax
	mov eax, 143
	mov dword ptr [rbp - 456], eax
	mov eax, 144
	mov dword ptr [rbp - 452], eax
	mov eax, 145
	mov dword ptr [rbp - 448], eax
	mov eax, 146
	mov dword ptr [rbp - 444], eax
	mov eax, 147
	mov dword ptr [rbp - 440], eax
	mov eax, 148
	mov dword ptr [rbp - 436], eax
	mov eax, 149
	mov dword ptr [rbp - 432], eax
	mov eax, 150
	mov dword ptr [rbp - 428], eax
	mov eax, 151
	mov dword ptr [rbp - 424], eax
	mov eax, 152
	mov dword ptr [rbp - 420], eax
	mov eax, 153
	mov dword ptr [rbp - 416], eax
	mov eax, 154
	mov dword ptr [rbp - 412], eax
	mov eax, 155
	mov dword ptr [rbp - 408], eax
	mov eax, 156
	mov dword ptr [rbp - 404], eax
	mov eax, 157
	mov dword ptr [rbp - 400], eax
	mov eax, 158
	mov dword ptr [rbp - 396], eax
	mov eax, 159
	mov dword ptr [rbp - 392], eax
	mov eax, 160
	mov dword ptr [rbp - 388], eax
	mov eax, 161
	mov dword ptr [rbp - 384], eax
	mov eax, 162
	mov dword ptr [rbp - 380], eax
	mov eax, 163
	mov dword ptr [rbp - 376], eax
	mov eax, 164
	mov dword ptr [rbp - 372], eax
	mov eax, 165
	mov dword ptr [rbp - 368], eax
	mov eax, 166
	mov dword ptr [rbp - 364], eax
	mov eax, 167
	mov dword ptr [rbp - 360], eax
	mov eax, 168
	mov dword ptr [rbp - 356], eax
	mov eax, 169
	mov dword ptr [rbp - 352], eax
	mov eax, 170
	mov dword ptr [rbp - 348], eax
	mov eax, 171
	mov dword ptr [rbp - 344], eax
	mov eax, 172
	mov dword ptr [rbp - 340], eax
	mov eax, 173
	mov dword ptr [rbp - 336], eax
	mov eax, 174
	mov dword ptr [rbp - 332], eax
	mov eax, 175
	mov dword ptr [rbp - 328], eax
	mov eax, 176
	mov dword ptr [rbp - 324], eax
	mov eax, 177
	mov dword ptr [rbp - 320], eax
	mov eax, 178
	mov dword ptr [rbp - 316], eax
	mov eax, 179
	mov dword ptr [rbp - 312], eax
	mov eax, 180
	mov dword ptr [rbp - 308], eax
	mov eax, 181
	mov dword ptr [rbp - 304], eax
	mov eax, 182
	mov dword ptr [rbp - 300], eax
	mov eax, 183
	mov dword ptr [rbp - 296], eax
	mov eax, 184
	mov dword ptr [rbp - 292], eax
	mov eax, 185
	mov dword ptr [rbp - 288], eax
	mov eax, 186
	mov dword ptr [rbp - 284], eax
	mov eax, 187
	mov dword ptr [rbp - 280], eax
	mov eax, 188
	mov dword ptr [rbp - 276], eax
	mov eax, 189
	mov dword ptr [rbp - 272], eax
	mov eax, 190
	mov dword ptr [rbp - 268], eax
	mov eax, 191
	mov dword ptr [rbp - 264], eax
	mov eax, 192
	mov dword ptr [rbp - 260], eax
	mov eax, 193
	mov dword ptr [rbp - 256], eax
	mov eax, 194
	mov dword ptr [rbp - 252], eax
	mov eax, 195
	mov dword ptr [rbp - 248], eax
	mov eax, 196
	mov dword ptr [rbp - 244], eax
	mov eax, 197
	mov dword ptr [rbp - 240], eax
	mov eax, 198
	mov dword ptr [rbp - 236], eax
	mov eax, 199
	mov dword ptr [rbp - 232], eax
	mov eax, 200
	mov dword ptr [rbp - 228], eax
	mov eax, 201
	mov dword ptr [rbp - 224], eax
	mov eax, 202
	mov dword ptr [rbp - 220], eax
	mov eax, 203
	mov dword ptr [rbp - 216], eax
	mov eax, 204
	mov dword ptr [rbp - 212], eax
	mov eax, 205
	mov dword ptr [rbp - 208], eax
	mov eax, 206
	mov dword ptr [rbp - 204], eax
	mov eax, 207
	mov dword ptr [rbp - 200], eax
	mov eax, 208
	mov dword ptr [rbp - 196], eax
	mov eax, 209
	mov dword ptr [rbp - 192], eax
	mov eax, 210
	mov dword ptr [rbp - 188], eax
	mov eax, 211
	mov dword ptr [rbp - 184], eax
	mov eax, 212
	mov dword ptr [rbp - 180], eax
	mov eax, 213
	mov dword ptr [rbp - 176], eax
	mov eax, 214
	mov dword ptr [rbp - 172], eax
	mov eax, 215
	mov dword ptr [rbp - 168], eax
	mov eax, 216
	mov dword ptr [rbp - 164], eax
	mov eax, 217
	mov dword ptr [rbp - 160], eax
	mov eax, 218
	mov dword ptr [rbp - 156], eax
	mov eax, 219
	mov dword ptr [rbp - 152], eax
	mov eax, 220
	mov dword ptr [rbp - 148], eax
	mov eax, 221
	mov dword ptr [rbp - 144], eax
	mov eax, 222
	mov dword ptr [rbp - 140], eax
	mov eax, 223
	mov dword ptr [rbp - 136], eax
	mov eax, 224
	mov dword ptr [rbp - 132], eax
	mov eax, 225
	mov dword ptr [rbp - 128], eax
	mov eax, 226
	mov dword ptr [rbp - 124], eax
	mov eax, 227
	mov dword ptr [rbp - 120], eax
	mov eax, 228
	mov dword ptr [rbp - 116], eax
	mov eax, 229
	mov dword ptr [rbp - 112], eax
	mov eax, 230
	mov dword ptr [rbp - 108], eax
	mov eax, 231
	mov dword ptr [rbp - 104], eax
	mov eax, 232
	mov dword ptr [rbp - 100], eax
	mov eax, 233
	mov dword ptr [rbp - 96], eax
	mov eax, 234
	mov dword ptr [rbp - 92], eax
	mov eax, 235
	mov dword ptr [rbp - 88], eax
	mov eax, 236
	mov dword ptr [rbp - 84], eax
	mov eax, 237
	mov dword ptr [rbp - 80], eax
	mov eax, 238
	mov dword ptr [rbp - 76], eax
	mov eax, 239
	mov dword ptr [rbp - 72], eax
	mov eax, 240
	mov dword ptr [rbp - 68], eax
	mov eax, 241
	mov dword ptr [rbp - 64], eax
	mov eax, 242
	mov dword ptr [rbp - 60], eax
	mov eax, 243
	mov dword ptr [rbp - 56], eax
	mov eax, 244
	mov dword ptr [rbp - 52], eax
	mov eax, 245
	mov dword ptr [rbp - 48], eax
	mov eax, 246
	mov dword ptr [rbp - 44], eax
	mov eax, 247
	mov dword ptr [rbp - 40], eax
	mov eax, 248
	mov dword ptr [rbp - 36], eax
	mov eax, 249
	mov dword ptr [rbp - 32], eax
	mov eax, 250
	mov dword ptr [rbp - 28], eax
	mov eax, 251
	mov dword ptr [rbp - 24], eax
	mov eax, 252
	mov dword ptr [rbp - 20], eax
	mov eax, 253
	mov dword ptr [rbp - 16], eax
	mov eax, 254
	mov dword ptr [rbp - 12], eax
	mov eax, 255
	mov dword ptr [rbp - 8], eax
	mov eax, 256
	mov dword ptr [rbp - 4], eax
	add rsp, 988
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
