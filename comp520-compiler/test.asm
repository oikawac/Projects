.data
# spilled registers heap allocation
label_38_:
.space 0

.data

.text
# Cailean Oikawa
# init fp to sp
or $fp,$sp,$zero
# jump to main:
jal label_37_
# exit syscall:
li $v0,10
syscall

.text
# mcmalloc
label_0_:
lw $a0,0($fp)
li $v0,9
syscall
sw $v0,12($fp)
jr $ra

.text
# read_i
label_1_:
li $v0,5
syscall
sw $v0,8($fp)
jr $ra

.text
# read_c
label_2_:
li $v0,12
syscall
sb $v0,8($fp)
jr $ra

.text
# print_c
label_3_:
lw $a0,0($fp)
li $v0,11
syscall
jr $ra

.text
# print_i
label_4_:
lw $a0,0($fp)
li $v0,1
syscall
jr $ra

.text
# print_s
label_5_:
lw $a0,0($fp)
li $v0,4
syscall
jr $ra

.text
# a
label_6_:
# local var (b): 
addi $sp,$sp,-60
# local var (c): 
addi $sp,$sp,-4
# local var (d): 
addi $sp,$sp,-4
# local var (e): 
addi $sp,$sp,-4
# local var (f): 
addi $sp,$sp,-4
# local var (g): 
addi $sp,$sp,-4
# local var (h): 
addi $sp,$sp,-4
# local var (i): 
addi $sp,$sp,-4
# local var (j): 
addi $sp,$sp,-4
# local var (k): 
addi $sp,$sp,-4
# local var (l): 
addi $sp,$sp,-52
# local var (m): 
addi $sp,$sp,-4
# local var (n): 
addi $sp,$sp,-4
# local var (o): 
addi $sp,$sp,-4
# local var (p): 
addi $sp,$sp,-4
# local var (q): 
addi $sp,$sp,-4
# local var (r): 
addi $sp,$sp,-4
# push registers: 
sw $s7,-4($sp)
sw $s2,-8($sp)
sw $t9,-12($sp)
sw $t4,-16($sp)
sw $s4,-20($sp)
sw $t7,-24($sp)
sw $t6,-28($sp)
addi $sp,$sp,-28
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# int literal:
li $s2,0
li $t9,4
mul $s2,$s2,$t9
add $s2,$s7,$s2
# int literal:
li $t9,4
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $t9,1
li $s7,4
mul $t9,$t9,$s7
add $s2,$s2,$t9
# int literal:
li $t9,32
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $t9,2
li $s7,4
mul $t9,$t9,$s7
add $s2,$s2,$t9
# int literal:
li $t9,247
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $t9,$fp,-60
# int literal:
li $s7,3
li $s2,4
mul $s7,$s7,$s2
add $s2,$t9,$s7
# int literal:
li $t9,212
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# int literal:
li $t9,4
li $s2,4
mul $t9,$t9,$s2
add $s2,$s7,$t9
# int literal:
li $t9,5
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# int literal:
li $t9,5
li $s2,4
mul $t9,$t9,$s2
add $s2,$s7,$t9
# int literal:
li $t9,35
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $t9,6
li $s7,4
mul $t9,$t9,$s7
add $t9,$s2,$t9
# int literal:
li $s2,6
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $t9,7
li $s7,4
mul $t9,$t9,$s7
add $t9,$s2,$t9
# int literal:
li $s2,1
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# int literal:
li $t9,8
li $s2,4
mul $t9,$t9,$s2
add $t9,$s7,$t9
# int literal:
li $s2,134
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $s7,9
li $t9,4
mul $s7,$s7,$t9
add $t9,$s2,$s7
# int literal:
li $s2,87
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $s7,10
li $t9,4
mul $s7,$s7,$t9
add $t9,$s2,$s7
# int literal:
li $s2,149
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $t9,$fp,-60
# int literal:
li $s7,11
li $s2,4
mul $s7,$s7,$s2
add $s2,$t9,$s7
# int literal:
li $t9,42
sw $t9,0($s2)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# int literal:
li $t9,12
li $s7,4
mul $t9,$t9,$s7
add $t9,$s2,$t9
# int literal:
li $s2,27
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# int literal:
li $s2,13
li $t9,4
mul $s2,$s2,$t9
add $t9,$s7,$s2
# int literal:
li $s2,15
sw $s2,0($t9)
# assign: 
# addr of array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# int literal:
li $s2,14
li $t9,4
mul $s2,$s2,$t9
add $t9,$s7,$s2
# int literal:
li $s2,4
sw $s2,0($t9)
# assign: 
# addr of var (c): 
addi $s2,$fp,-64
# int literal:
li $t9,4
sw $t9,0($s2)
# assign: 
# addr of var (d): 
addi $s2,$fp,-68
# int literal:
li $t9,32
sw $t9,0($s2)
# assign: 
# addr of var (e): 
addi $t9,$fp,-72
# int literal:
li $s2,247
sw $s2,0($t9)
# assign: 
# addr of var (f): 
addi $t9,$fp,-76
# int literal:
li $s2,212
sw $s2,0($t9)
# assign: 
# addr of var (g): 
addi $s2,$fp,-80
# int literal:
li $t9,5
sw $t9,0($s2)
# assign: 
# addr of var (h): 
addi $s2,$fp,-84
# int literal:
li $t9,35
sw $t9,0($s2)
# assign: 
# addr of var (i): 
addi $t9,$fp,-88
# int literal:
li $s2,6
sw $s2,0($t9)
# assign: 
# addr of var (j): 
addi $t9,$fp,-92
# int literal:
li $s2,1
sw $s2,0($t9)
# assign: 
# addr of var (k): 
addi $s2,$fp,-96
# int literal:
li $t9,134
sw $t9,0($s2)
# assign: 
# addr of var (m): 
addi $t9,$fp,-152
# int literal:
li $s2,0
sw $s2,0($t9)
# while: 
label_7_:
# lt:
# var m: 
# addr of var (m): 
addi $t9,$fp,-152
lw $t9,0($t9)
# int literal:
li $s2,13
slt $t9,$t9,$s2
beqz $t9,label_8_
# assign: 
# addr of array access: 
# var l: 
# addr of var (l): 
addi $s7,$fp,-148
# var m: 
# addr of var (m): 
addi $t9,$fp,-152
lw $s2,0($t9)
li $t9,4
mul $s2,$s2,$t9
add $s2,$s7,$s2
# array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# var m: 
# addr of var (m): 
addi $t9,$fp,-152
lw $t4,0($t9)
li $t9,4
mul $t4,$t4,$t9
add $s7,$s7,$t4
lw $t9,0($s7)
sw $t9,0($s2)
# assign: 
# addr of var (m): 
addi $s2,$fp,-152
# add:
# var m: 
# addr of var (m): 
addi $t9,$fp,-152
lw $s7,0($t9)
# int literal:
li $t9,1
add $t9,$s7,$t9
sw $t9,0($s2)
b label_7_
label_8_:
# assign: 
# addr of var (n): 
addi $s7,$fp,-156
# add:
# int literal:
li $t9,4
# int literal:
li $s2,4
add $t9,$t9,$s2
sw $t9,0($s7)
# assign: 
# addr of var (o): 
addi $s2,$fp,-160
# add:
# int literal:
li $t9,32
# int literal:
li $s7,32
add $t9,$t9,$s7
sw $t9,0($s2)
# assign: 
# addr of var (p): 
addi $s7,$fp,-164
# sub:
# int literal:
li $s2,247
# int literal:
li $t9,247
sub $t9,$s2,$t9
sw $t9,0($s7)
# assign: 
# addr of var (q): 
addi $s2,$fp,-168
# int literal:
li $t9,0
sw $t9,0($s2)
# assign: 
# addr of var (r): 
addi $t9,$fp,-172
# int literal:
li $s2,0
sw $s2,0($t9)
# while: 
label_9_:
# lt:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,248
slt $t9,$t9,$s2
beqz $t9,label_10_
# local var (s): 
addi $sp,$sp,-4
# if: 
# lt:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s2,0($t9)
# int literal:
li $t9,158
slt $t9,$s2,$t9
beqz $t9,label_11_
# then: 
# assign: 
# addr of var (s): 
addi $s4,$fp,-176
# sub:
# int literal:
li $t7,0
# sub:
# int literal:
li $t4,0
# sub:
# sub:
# array access: 
# var b: 
# addr of var (b): 
addi $t9,$fp,-60
# div:
# var q: 
# addr of var (q): 
addi $s2,$fp,-168
lw $s7,0($s2)
# int literal:
li $s2,15
div $s7,$s2
mflo $s7
li $s2,4
mul $s7,$s7,$s2
add $t9,$t9,$s7
lw $t9,0($t9)
# int literal:
li $s2,4
sub $s2,$t9,$s2
# add:
# array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# mod:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t6,0($t9)
# int literal:
li $t9,15
div $t6,$t9
mfhi $t6
li $t9,4
mul $t6,$t6,$t9
add $s7,$s7,$t6
lw $t9,0($s7)
# int literal:
li $s7,3
add $t9,$t9,$s7
sub $t9,$s2,$t9
sub $t9,$t4,$t9
sub $t9,$t7,$t9
sw $t9,0($s4)
b label_12_
# else: 
label_11_:
# local var (t): 
addi $sp,$sp,-4
# local var (u): 
addi $sp,$sp,-4
# local var (v): 
addi $sp,$sp,-4
# if: 
# lt:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $t9,$t9,$s2
# int literal:
li $s2,3
div $t9,$s2
mflo $s2
# int literal:
li $t9,15
slt $t9,$s2,$t9
beqz $t9,label_13_
# then: 
# assign: 
# addr of var (t): 
addi $t4,$fp,-180
# array access: 
# var b: 
# addr of var (b): 
addi $s7,$fp,-60
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $s2,$t9,$s2
# int literal:
li $t9,3
div $s2,$t9
mflo $t9
li $s2,4
mul $t9,$t9,$s2
add $s7,$s7,$t9
lw $t9,0($s7)
sw $t9,0($t4)
b label_14_
# else: 
label_13_:
# assign: 
# addr of var (t): 
addi $s2,$fp,-180
# array access: 
# var b: 
# addr of var (b): 
addi $t4,$fp,-60
# sub:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s7,0($t9)
# int literal:
li $t9,158
sub $s7,$s7,$t9
# int literal:
li $t9,3
div $s7,$t9
mflo $s7
# int literal:
li $t9,15
sub $s7,$s7,$t9
li $t9,4
mul $s7,$s7,$t9
add $t4,$t4,$s7
lw $t9,0($t4)
sw $t9,0($s2)
label_14_:
# if: 
# lt:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $s2,$t9,$s2
# int literal:
li $t9,3
div $s2,$t9
mflo $s2
# int literal:
li $t9,9
slt $t9,$s2,$t9
beqz $t9,label_15_
# then: 
# local var (w): 
addi $sp,$sp,-4
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $s2,$t9,$s2
# int literal:
li $t9,3
div $s2,$t9
mflo $s2
# int literal:
li $t9,0
subu $s2,$s2,$t9
li $t9,1
sltu $s2,$s2,$t9
beqz $s2,label_17_
# then: 
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var c: 
# addr of var (c): 
addi $t9,$fp,-64
lw $t9,0($t9)
sw $t9,0($s2)
b label_18_
# else: 
label_17_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $t9,$t9,$s2
# int literal:
li $s2,3
div $t9,$s2
mflo $t9
# int literal:
li $s2,1
subu $t9,$t9,$s2
li $s2,1
sltu $t9,$t9,$s2
beqz $t9,label_19_
# then: 
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var d: 
# addr of var (d): 
addi $t9,$fp,-68
lw $t9,0($t9)
sw $t9,0($s2)
b label_20_
# else: 
label_19_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $s2,$t9,$s2
# int literal:
li $t9,3
div $s2,$t9
mflo $s2
# int literal:
li $t9,2
subu $s2,$s2,$t9
li $t9,1
sltu $s2,$s2,$t9
beqz $s2,label_21_
# then: 
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var e: 
# addr of var (e): 
addi $t9,$fp,-72
lw $t9,0($t9)
sw $t9,0($s2)
b label_22_
# else: 
label_21_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $t9,$t9,$s2
# int literal:
li $s2,3
div $t9,$s2
mflo $s2
# int literal:
li $t9,3
subu $s2,$s2,$t9
li $t9,1
sltu $s2,$s2,$t9
beqz $s2,label_23_
# then: 
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var f: 
# addr of var (f): 
addi $t9,$fp,-76
lw $t9,0($t9)
sw $t9,0($s2)
b label_24_
# else: 
label_23_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $s2,$t9,$s2
# int literal:
li $t9,3
div $s2,$t9
mflo $t9
# int literal:
li $s2,4
subu $t9,$t9,$s2
li $s2,1
sltu $t9,$t9,$s2
beqz $t9,label_25_
# then: 
# assign: 
# addr of var (w): 
addi $t9,$fp,-192
# var g: 
# addr of var (g): 
addi $s2,$fp,-80
lw $s2,0($s2)
sw $s2,0($t9)
b label_26_
# else: 
label_25_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s2,0($t9)
# int literal:
li $t9,158
sub $t9,$s2,$t9
# int literal:
li $s2,3
div $t9,$s2
mflo $t9
# int literal:
li $s2,5
subu $t9,$t9,$s2
li $s2,1
sltu $t9,$t9,$s2
beqz $t9,label_27_
# then: 
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var h: 
# addr of var (h): 
addi $t9,$fp,-84
lw $t9,0($t9)
sw $t9,0($s2)
b label_28_
# else: 
label_27_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $s2,$t9,$s2
# int literal:
li $t9,3
div $s2,$t9
mflo $s2
# int literal:
li $t9,6
subu $s2,$s2,$t9
li $t9,1
sltu $s2,$s2,$t9
beqz $s2,label_29_
# then: 
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var i: 
# addr of var (i): 
addi $t9,$fp,-88
lw $t9,0($t9)
sw $t9,0($s2)
b label_30_
# else: 
label_29_:
# if: 
# eq:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s2,0($t9)
# int literal:
li $t9,158
sub $t9,$s2,$t9
# int literal:
li $s2,3
div $t9,$s2
mflo $s2
# int literal:
li $t9,7
subu $t9,$s2,$t9
li $s2,1
sltu $t9,$t9,$s2
beqz $t9,label_31_
# then: 
# assign: 
# addr of var (w): 
addi $t9,$fp,-192
# var j: 
# addr of var (j): 
addi $s2,$fp,-92
lw $s2,0($s2)
sw $s2,0($t9)
b label_32_
# else: 
label_31_:
# assign: 
# addr of var (w): 
addi $s2,$fp,-192
# var k: 
# addr of var (k): 
addi $t9,$fp,-96
lw $t9,0($t9)
sw $t9,0($s2)
label_32_:
label_30_:
label_28_:
label_26_:
label_24_:
label_22_:
label_20_:
label_18_:
# assign: 
# addr of var (u): 
addi $s2,$fp,-184
# var w: 
# addr of var (w): 
addi $t9,$fp,-192
lw $t9,0($t9)
sw $t9,0($s2)
b label_16_
# else: 
label_15_:
# assign: 
# addr of var (u): 
addi $s7,$fp,-184
# array access: 
# var b: 
# addr of var (b): 
addi $t4,$fp,-60
# mod:
# sub:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $t9,$t9,$s2
# int literal:
li $s2,3
div $t9,$s2
mflo $t9
# int literal:
li $s2,9
sub $t9,$t9,$s2
# int literal:
li $s2,15
div $t9,$s2
mfhi $s2
li $t9,4
mul $s2,$s2,$t9
add $t4,$t4,$s2
lw $t9,0($t4)
sw $t9,0($s7)
label_16_:
# if: 
# eq:
# mod:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s2,0($t9)
# int literal:
li $t9,158
sub $s2,$s2,$t9
# int literal:
li $t9,3
div $s2,$t9
mfhi $t9
# int literal:
li $s2,0
subu $t9,$t9,$s2
li $s2,1
sltu $t9,$t9,$s2
beqz $t9,label_33_
# then: 
# assign: 
# addr of var (v): 
addi $s2,$fp,-188
# var n: 
# addr of var (n): 
addi $t9,$fp,-156
lw $t9,0($t9)
sw $t9,0($s2)
b label_34_
# else: 
label_33_:
# if: 
# eq:
# mod:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s2,158
sub $t9,$t9,$s2
# int literal:
li $s2,3
div $t9,$s2
mfhi $t9
# int literal:
li $s2,1
subu $t9,$t9,$s2
li $s2,1
sltu $t9,$t9,$s2
beqz $t9,label_35_
# then: 
# assign: 
# addr of var (v): 
addi $s2,$fp,-188
# var o: 
# addr of var (o): 
addi $t9,$fp,-160
lw $t9,0($t9)
sw $t9,0($s2)
b label_36_
# else: 
label_35_:
# assign: 
# addr of var (v): 
addi $s2,$fp,-188
# var p: 
# addr of var (p): 
addi $t9,$fp,-164
lw $t9,0($t9)
sw $t9,0($s2)
label_36_:
label_34_:
# assign: 
# addr of var (s): 
addi $t6,$fp,-176
# sub:
# add:
# add:
# var t: 
# addr of var (t): 
addi $t9,$fp,-180
lw $s4,0($t9)
# mul:
# array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# div:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s7,0($t9)
# int literal:
li $t9,158
sub $s7,$s7,$t9
# int literal:
li $t9,3
div $s7,$t9
mflo $s7
# int literal:
li $t9,15
div $s7,$t9
mflo $s7
li $t9,4
mul $s7,$s7,$t9
add $s2,$s2,$s7
lw $t4,0($s2)
# array access: 
# var b: 
# addr of var (b): 
addi $s2,$fp,-60
# mod:
# div:
# sub:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $t9,0($t9)
# int literal:
li $s7,158
sub $s7,$t9,$s7
# int literal:
li $t9,3
div $s7,$t9
mflo $s7
# int literal:
li $t9,15
div $s7,$t9
mfhi $t9
li $s7,4
mul $t9,$t9,$s7
add $s2,$s2,$t9
lw $t9,0($s2)
mul $t9,$t4,$t9
add $t9,$s4,$t9
# var u: 
# addr of var (u): 
addi $s2,$fp,-184
lw $s2,0($s2)
add $s2,$t9,$s2
# var v: 
# addr of var (v): 
addi $t9,$fp,-188
lw $t9,0($t9)
sub $t9,$s2,$t9
sw $t9,0($t6)
label_12_:
# assign: 
# addr of var (r): 
addi $s7,$fp,-172
# add:
# var s: 
# addr of var (s): 
addi $t9,$fp,-176
lw $s2,0($t9)
# var r: 
# addr of var (r): 
addi $t9,$fp,-172
lw $t9,0($t9)
add $t9,$s2,$t9
sw $t9,0($s7)
# assign: 
# addr of var (q): 
addi $s2,$fp,-168
# add:
# var q: 
# addr of var (q): 
addi $t9,$fp,-168
lw $s7,0($t9)
# int literal:
li $t9,1
add $t9,$s7,$t9
sw $t9,0($s2)
b label_9_
label_10_:
# return: 
addi $s2,$fp,8
# var r: 
# addr of var (r): 
addi $t9,$fp,-172
lw $t9,0($t9)
sw $t9,0($s2)
addi $sp,$fp,-172
# pop registers: 
lw $s7,-4($sp)
lw $s2,-8($sp)
lw $t9,-12($sp)
lw $t4,-16($sp)
lw $s4,-20($sp)
lw $t7,-24($sp)
lw $t6,-28($sp)
jr $ra
# return: 
addi $sp,$fp,-172
# pop registers: 
lw $s7,-4($sp)
lw $s2,-8($sp)
lw $t9,-12($sp)
lw $t4,-16($sp)
lw $s4,-20($sp)
lw $t7,-24($sp)
lw $t6,-28($sp)
jr $ra

.text
# main
label_37_:
# push registers: 
sw $t9,-4($sp)
addi $sp,$sp,-4
# fun call (print_i): 
addi $sp,$sp,-12
# save caller state: 
sw $ra,8($sp)
sw $fp,4($sp)
# pass args: 
# arg: (NUMBER): 
# fun call (a): 
addi $sp,$sp,-12
# save caller state: 
sw $ra,4($sp)
sw $fp,0($sp)
# pass args: 
or $fp,$sp,$zero
jal label_6_
# post return: 
lw $t9,8($fp)
lw $ra,4($fp)
addi $sp,$fp,12
lw $fp,0($fp)
sw $t9,0($sp)
or $fp,$sp,$zero
jal label_4_
# post return: 
lw $ra,8($fp)
addi $sp,$fp,12
lw $fp,4($fp)
# return: 
addi $sp,$fp,0
# pop registers: 
lw $t9,-4($sp)
jr $ra

