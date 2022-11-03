.data
# spilled registers heap allocation
label_32_:
.space 20

.text
# A compiled version of array_complex_reduction_8.c that has not yet gone through the register allocator.

.text

.data

.text
jal main

.text
a:
addu $fp,$sp,$zero
addi $sp,$sp,-192
addi $t4,$fp,-60
li $t9,0
li $s1,4
mul $t9,$s1,$t9
addu $t9,$t4,$t9
li $t4,4
sw $t4,0($t9)
addi $s1,$fp,-60
li $t9,1
li $t4,4
mul $t9,$t4,$t9
addu $t9,$s1,$t9
li $t4,32
sw $t4,0($t9)
addi $t4,$fp,-60
li $t9,2
li $s1,4
mul $t9,$s1,$t9
addu $t9,$t4,$t9
li $t4,247
sw $t4,0($t9)
addi $t4,$fp,-60
li $s1,3
li $t9,4
mul $t9,$t9,$s1
addu $t9,$t4,$t9
li $t4,212
sw $t4,0($t9)
addi $s1,$fp,-60
li $t4,4
li $t9,4
mul $t9,$t9,$t4
addu $t9,$s1,$t9
li $t4,5
sw $t4,0($t9)
addi $t9,$fp,-60
li $t4,5
li $s1,4
mul $s1,$s1,$t4
addu $s1,$t9,$s1
li $t4,35
sw $t4,0($s1)
addi $t9,$fp,-60
li $s1,6
li $t4,4
mul $s1,$t4,$s1
addu $s1,$t9,$s1
li $t4,6
sw $t4,0($s1)
addi $t9,$fp,-60
li $t4,7
li $s1,4
mul $s1,$s1,$t4
addu $s1,$t9,$s1
li $t4,1
sw $t4,0($s1)
addi $t9,$fp,-60
li $s1,8
li $t4,4
mul $s1,$t4,$s1
addu $s1,$t9,$s1
li $t4,134
sw $t4,0($s1)
addi $t4,$fp,-60
li $s1,9
li $t9,4
mul $t9,$t9,$s1
addu $t9,$t4,$t9
li $t4,87
sw $t4,0($t9)
addi $t4,$fp,-60
li $t9,10
li $s1,4
mul $t9,$s1,$t9
addu $t9,$t4,$t9
li $t4,149
sw $t4,0($t9)
addi $s1,$fp,-60
li $t9,11
li $t4,4
mul $t9,$t4,$t9
addu $t9,$s1,$t9
li $t4,42
sw $t4,0($t9)
addi $s1,$fp,-60
li $t4,12
li $t9,4
mul $t9,$t9,$t4
addu $t9,$s1,$t9
li $t4,27
sw $t4,0($t9)
addi $t9,$fp,-60
li $t4,13
li $s1,4
mul $s1,$s1,$t4
addu $s1,$t9,$s1
li $t4,15
sw $t4,0($s1)
addi $t9,$fp,-60
li $t4,14
li $s1,4
mul $s1,$s1,$t4
addu $s1,$t9,$s1
li $t4,4
sw $t4,0($s1)
li $t4,4
addi $t4,$t4,0
li $t9,32
addi $t7,$t9,0
li $t9,247
addi $s4,$t9,0
li $t9,212
addi $t0,$t9,0
la $t1,label_32_
sw $t0,4($t1)
li $t9,5
addi $t5,$t9,0
li $t9,35
addi $s1,$t9,0
li $t9,6
addi $t0,$t9,0
sw $t0,12($t1)
li $t9,1
addi $t0,$t9,0
sw $t0,0($t1)
li $t9,134
addi $t0,$t9,0
sw $t0,16($t1)
li $t9,0
addi $s0,$t9,0
label_2_while_start:
li $t9,13
slt $t9,$s0,$t9
beqz $t9,label_3_while_end
addi $s2,$fp,-112
li $t9,4
mul $s6,$t9,$s0
addu $s6,$s2,$s6
addi $s2,$fp,-60
li $t9,4
mul $t9,$s0,$t9
addu $t9,$s2,$t9
lw $t9,0($t9)
sw $t9,0($s6)
li $t9,1
addu $t9,$s0,$t9
addi $s0,$t9,0
b label_2_while_start
label_3_while_end:
li $t9,4
li $s2,4
addu $t9,$t9,$s2
addi $s6,$t9,0
li $s2,32
li $t9,32
addu $t9,$s2,$t9
addi $s0,$t9,0
li $s2,247
li $t9,247
sub $t9,$s2,$t9
addi $t0,$t9,0
la $t1,label_32_
sw $t0,8($t1)
li $t9,0
addi $t3,$t9,0
li $t9,0
addi $s7,$t9,0
label_4_while_start:
li $t9,248
slt $t9,$t3,$t9
beqz $t9,label_5_while_end
li $t9,158
slt $t9,$t3,$t9
beqz $t9,label_6_true_end
li $s2,0
li $s3,0
addi $t6,$fp,-60
li $t9,15
div $t3,$t9
mflo $t9
li $t2,4
mul $t9,$t9,$t2
addu $t9,$t6,$t9
lw $t9,0($t9)
li $t6,4
sub $t8,$t9,$t6
addi $t2,$fp,-60
li $t9,15
div $t3,$t9
mfhi $t9
li $t6,4
mul $t9,$t9,$t6
addu $t9,$t2,$t9
lw $t6,0($t9)
li $t9,3
addu $t9,$t6,$t9
sub $t9,$t8,$t9
sub $t9,$s3,$t9
sub $t9,$s2,$t9
addi $t9,$t9,0
b label_7_if_end
label_6_true_end:
li $t9,158
sub $s2,$t3,$t9
li $t9,3
div $s2,$t9
mflo $s2
li $t9,15
slt $t9,$s2,$t9
beqz $t9,label_8_true_end
addi $s2,$fp,-60
li $t9,158
sub $s3,$t3,$t9
li $t9,3
div $s3,$t9
mflo $t9
li $s3,4
mul $t9,$t9,$s3
addu $t9,$s2,$t9
lw $t9,0($t9)
addi $t2,$t9,0
b label_9_if_end
label_8_true_end:
addi $s2,$fp,-60
li $t9,158
sub $s3,$t3,$t9
li $t9,3
div $s3,$t9
mflo $t9
li $s3,15
sub $t9,$t9,$s3
li $s3,4
mul $t9,$t9,$s3
addu $t9,$s2,$t9
lw $t9,0($t9)
addi $t2,$t9,0
label_9_if_end:
li $t9,158
sub $t9,$t3,$t9
li $s2,3
div $t9,$s2
mflo $t9
li $s2,9
slt $t9,$t9,$s2
beqz $t9,label_10_true_end
li $t9,158
sub $t9,$t3,$t9
li $s2,3
div $t9,$s2
mflo $s2
li $t9,0
xor $t9,$s2,$t9
sltiu $t9,$t9,1
beqz $t9,label_12_true_end
addi $t9,$t4,0
b label_13_if_end
label_12_true_end:
li $t9,158
sub $s2,$t3,$t9
li $t9,3
div $s2,$t9
mflo $t9
li $s2,1
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_14_true_end
addi $t9,$t7,0
b label_15_if_end
label_14_true_end:
li $t9,158
sub $t9,$t3,$t9
li $s2,3
div $t9,$s2
mflo $s2
li $t9,2
xor $t9,$s2,$t9
sltiu $t9,$t9,1
beqz $t9,label_16_true_end
addi $t9,$s4,0
b label_17_if_end
label_16_true_end:
li $t9,158
sub $t9,$t3,$t9
li $s2,3
div $t9,$s2
mflo $s2
li $t9,3
xor $t9,$s2,$t9
sltiu $t9,$t9,1
beqz $t9,label_18_true_end
la $t1,label_32_
lw $t0,4($t1)
addi $t9,$t0,0
b label_19_if_end
label_18_true_end:
li $t9,158
sub $s2,$t3,$t9
li $t9,3
div $s2,$t9
mflo $t9
li $s2,4
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_20_true_end
addi $t9,$t5,0
b label_21_if_end
label_20_true_end:
li $t9,158
sub $s2,$t3,$t9
li $t9,3
div $s2,$t9
mflo $t9
li $s2,5
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_22_true_end
addi $t9,$s1,0
b label_23_if_end
label_22_true_end:
li $t9,158
sub $s2,$t3,$t9
li $t9,3
div $s2,$t9
mflo $t9
li $s2,6
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_24_true_end
la $t1,label_32_
lw $t0,12($t1)
addi $t9,$t0,0
b label_25_if_end
label_24_true_end:
li $t9,158
sub $t9,$t3,$t9
li $s2,3
div $t9,$s2
mflo $t9
li $s2,7
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_26_true_end
la $t1,label_32_
lw $t0,0($t1)
addi $t9,$t0,0
b label_27_if_end
label_26_true_end:
la $t1,label_32_
lw $t0,16($t1)
addi $t9,$t0,0
label_27_if_end:
label_25_if_end:
label_23_if_end:
label_21_if_end:
label_19_if_end:
label_17_if_end:
label_15_if_end:
label_13_if_end:
addi $t6,$t9,0
b label_11_if_end
label_10_true_end:
addi $s2,$fp,-60
li $t9,158
sub $t9,$t3,$t9
li $s3,3
div $t9,$s3
mflo $t9
li $s3,9
sub $t9,$t9,$s3
li $s3,15
div $t9,$s3
mfhi $s3
li $t9,4
mul $t9,$s3,$t9
addu $t9,$s2,$t9
lw $t9,0($t9)
addi $t6,$t9,0
label_11_if_end:
li $t9,158
sub $t9,$t3,$t9
li $s2,3
div $t9,$s2
mfhi $t9
li $s2,0
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_28_true_end
addi $t8,$s6,0
b label_29_if_end
label_28_true_end:
li $t9,158
sub $s2,$t3,$t9
li $t9,3
div $s2,$t9
mfhi $t9
li $s2,1
xor $t9,$t9,$s2
sltiu $t9,$t9,1
beqz $t9,label_30_true_end
addi $t8,$s0,0
b label_31_if_end
label_30_true_end:
la $t1,label_32_
lw $t0,8($t1)
addi $t8,$t0,0
label_31_if_end:
label_29_if_end:
addi $s2,$fp,-60
li $t9,158
sub $s3,$t3,$t9
li $t9,3
div $s3,$t9
mflo $s3
li $t9,15
div $s3,$t9
mflo $s3
li $t9,4
mul $t9,$s3,$t9
addu $t9,$s2,$t9
lw $s2,0($t9)
addi $s3,$fp,-60
li $t9,158
sub $t9,$t3,$t9
li $s5,3
div $t9,$s5
mflo $s5
li $t9,15
div $s5,$t9
mfhi $s5
li $t9,4
mul $t9,$s5,$t9
addu $t9,$s3,$t9
lw $t9,0($t9)
mul $t9,$s2,$t9
addu $t9,$t2,$t9
addu $t9,$t9,$t6
sub $t9,$t9,$t8
addi $t9,$t9,0
label_7_if_end:
addu $t9,$t9,$s7
addi $s7,$t9,0
li $t9,1
addu $t9,$t3,$t9
addi $t3,$t9,0
b label_4_while_start
label_5_while_end:
sw $s7,0($fp)
jr $ra
addi $sp,$fp,0
jr $ra

.text
.globl main
main:
addu $fp,$sp,$zero
addi $sp,$sp,-4
# push registers (alternative): 
sw $t4,-4($sp)
addi $sp,$sp,-4
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
jal a
addu $sp,$fp,$zero
lw $fp,4($sp)
lw $t4,0($sp)
sw $t4,-4($fp)
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
# pop registers (alternative): 
lw $t4,0($sp)
addi $sp,$sp,4
lw $t4,-4($fp)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,1
addu $a0,$t4,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi $sp,$fp,0
li $v0,10
syscall

