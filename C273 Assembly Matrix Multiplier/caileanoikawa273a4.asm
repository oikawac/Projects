# TODO: CAILEAN OIKAWA 260836366
# TODO: ADD OTHER COMMENTS YOU HAVE HERE AT THE TOP OF THIS FILE
# TODO: SEE LABELS FOR PROCEDURES YOU MUST IMPLEMENT AT THE BOTTOM OF THIS FILE

.data
TestNumber:	.word 1		# TODO: Which test to run!
				# 0 compare matrices stored in files Afname and Bfname
				# 1 test Proc using files A through D named below
				# 2 compare MADD1 and MADD2 with random matrices of size Size
				
Proc:		MADD3		# Procedure used by test 2, set to MADD1 or MADD2		
				
Size:		.word 64		# matrix size (MUST match size of matrix loaded for test 0 and 1)

Afname: 		.asciiz "A64.bin"
Bfname: 		.asciiz "B64.bin"
Cfname:		.asciiz "C64.bin"
Dfname:	 	.asciiz "D64.bin"

#################################################################
# Main function for testing assignment objectives.
# Modify this function as needed to complete your assignment.
# Note that the TA will ultimately use a different testing program.
.text
main:	la $t0 TestNumber
		lw $t0 ($t0)
		beq $t0 0 compareMatrix
		beq $t0 1 testFromFile
		beq $t0 2 compareMADD
		li $v0 10 # exit if the test number is out of range
        syscall	

compareMatrix:	
		la $s7 Size	
		lw $s7 ($s7)		# Let $s7 be the matrix size n

		move $a0 $s7
		jal mallocMatrix		# allocate heap memory and load matrix A
		move $s0 $v0		# $s0 is a pointer to matrix A
		la $a0 Afname
		move $a1 $s7
		move $a2 $s7
		move $a3 $s0
		jal loadMatrix
	
		move $a0 $s7
		jal mallocMatrix		# allocate heap memory and load matrix B
		move $s1 $v0		# $s1 is a pointer to matrix B
		la $a0 Bfname
		move $a1 $s7
		move $a2 $s7
		move $a3 $s1
		jal loadMatrix
	
		move $a0 $s0
		move $a1 $s1
		move $a2 $s7
		jal check
		
		li $v0 10      	# load exit call code 10 into $v0
        syscall         	# call operating system to exit	

testFromFile:	
		la $s7 Size	
		lw $s7 ($s7)		# Let $s7 be the matrix size n

		move $a0 $s7
		jal mallocMatrix		# allocate heap memory and load matrix A
		move $s0 $v0		# $s0 is a pointer to matrix A
		la $a0 Afname
		move $a1 $s7
		move $a2 $s7
		move $a3 $s0
		jal loadMatrix
	
		move $a0 $s7
		jal mallocMatrix		# allocate heap memory and load matrix B
		move $s1 $v0		# $s1 is a pointer to matrix B
		la $a0 Bfname
		move $a1 $s7
		move $a2 $s7
		move $a3 $s1
		jal loadMatrix
	
		move $a0 $s7
		jal mallocMatrix		# allocate heap memory and load matrix C
		move $s2 $v0		# $s2 is a pointer to matrix C
		la $a0 Cfname
		move $a1 $s7
		move $a2 $s7
		move $a3 $s2
		jal loadMatrix
	
		move $a0 $s7
		jal mallocMatrix		# allocate heap memory and load matrix D
		move $s3 $v0		# $s3 is a pointer to matrix D
		la $a0 Dfname
		move $a1 $s7
		move $a2 $s7
		move $a3 $s3
		jal loadMatrix		# D is the answer, i.e., D = AB+C
	
		# TODO: add your testing code here
		
		move $a0, $s0	# A
		move $a1, $s1	# B
		move $a2, $s2	# C
		move $a3, $s7	# n
		
		la $ra ReturnHere
		la $t0 Proc	# function pointer
		lw $t0 ($t0)	
		jr $t0		# like a jal to MADD1 or MADD2 depending on Proc definition

ReturnHere:	
		move $a0 $s2	# C
		move $a1 $s3	# D
		move $a2 $s7	# n
		jal check	# check the answer

		li $v0, 10      	# load exit call code 10 into $v0
	    syscall         	# call operating system to exit	

compareMADD:	
		la $s7 Size
		lw $s7 ($s7)	# n is loaded from Size
		mul $s4 $s7 $s7	# n^2
		sll $s5 $s4 2	# n^2 * 4

		move $a0 $s5
		li   $v0 9	# malloc A
		syscall	
		move $s0 $v0
		move $a0 $s5	# malloc B
		li   $v0 9
		syscall
		move $s1 $v0
		move $a0 $s5	# malloc C1
		li   $v0 9
		syscall
		move $s2 $v0
		move $a0 $s5	# malloc C2
		li   $v0 9
		syscall
		move $s3 $v0	
	
		move $a0 $s0	# A
		move $a1 $s4	# n^2
		jal  fillRandom	# fill A with random floats
		move $a0 $s1	# B
		move $a1 $s4	# n^2
		jal  fillRandom	# fill A with random floats
		move $a0 $s2	# C1
		move $a1 $s4	# n^2
		jal  fillZero	# fill A with random floats
		move $a0 $s3	# C2
		move $a1 $s4	# n^2
		jal  fillZero	# fill A with random floats

		move $a0 $s0	# A
		move $a1 $s1	# B
		move $a2 $s2	# C1	# note that we assume C1 to contain zeros !
		move $a3 $s7	# n
		jal MADD1

		move $a0 $s0	# A
		move $a1 $s1	# B
		move $a2 $s3	# C2	# note that we assume C2 to contain zeros !
		move $a3 $s7	# n
		jal MADD2

		move $a0 $s2	# C1
		move $a1 $s3	# C2
		move $a2 $s7	# n
		jal check	# check that they match
	
		li $v0 10      	# load exit call code 10 into $v0
        syscall         	# call operating system to exit	

###############################################################
# mallocMatrix( int N )
# Allocates memory for an N by N matrix of floats
# The pointer to the memory is returned in $v0	
mallocMatrix: 	
		mul  $a0, $a0, $a0	# Let $s5 be n squared
		sll  $a0, $a0, 2		# Let $s4 be 4 n^2 bytes
		li   $v0, 9		
		syscall			# malloc A
		jr $ra
	
###############################################################
# loadMatrix( char* filename, int width, int height, float* buffer )
.data
errorMessage: .asciiz "FILE NOT FOUND" 
.text
loadMatrix:	mul $t0 $a1 $a2 	# words to read (width x height) in a2
		sll $t0 $t0  2	  	# multiply by 4 to get bytes to read
		li $a1  0     		# flags (0: read, 1: write)
		li $a2  0     		# mode (unused)
		li $v0  13    		# open file, $a0 is null-terminated string of file name
		syscall
		slti $t1 $v0 0
		beq $t1 $0 fileFound
		la $a0 errorMessage
		li $v0 4
		syscall		  	# print error message
		li $v0 10         	# and then exit
		syscall		
fileFound:	move $a0 $v0     	# file descriptor (negative if error) as argument for read
  		move $a1 $a3     	# address of buffer in which to write
		move $a2 $t0	  	# number of bytes to read
		li  $v0 14       	# system call for read from file
		syscall           	# read from file
		# $v0 contains number of characters read (0 if end-of-file, negative if error).
                	# We'll assume that we do not need to be checking for errors!
		# Note, the bitmap display doesn't update properly on load, 
		# so let's go touch each memory address to refresh it!
		move $t0 $a3	# start address
		add $t1 $a3 $a2  	# end address
loadloop:	lw $t2 ($t0)
		sw $t2 ($t0)
		addi $t0 $t0 4
		bne $t0 $t1 loadloop		
		li $v0 16	# close file ($a0 should still be the file descriptor)
		syscall
		jr $ra	

##########################################################
# Fills the matrix $a0, which has $a1 entries, with random numbers
fillRandom:	li $v0 43
		syscall		# random float, and assume $a0 unmodified!!
		swc1 $f0 0($a0)
		addi $a0 $a0 4
		addi $a1 $a1 -1
		bne  $a1 $zero fillRandom
		jr $ra

##########################################################
# Fills the matrix $a0 , which has $a1 entries, with zero
fillZero:	
		sw $zero 0($a0)	# $zero is zero single precision float
		addi $a0 $a0 4
		addi $a1 $a1 -1
		bne  $a1 $zero fillZero
		jr $ra



######################################################
# TODO: void subtract( float* A, float* B, float* C, int N )  C = A - B 
# we assume pointer to A is in $a0 and pointer to B is in $a1
# and pointer to C is in $a2 and size N is in $a3
subtract:
		mul $t7 $a3 $a3
		move $t0 $a0
		move $t1 $a1
		move $t2 $a2
subtractMainLoop:
		lwc1 $f3 0($t0)
		lwc1 $f4 0($t1)
		sub.s $f5 $f3 $f4
		swc1 $f5 0($t2)
		addi $t0 $t0 4
		addi $t1 $t1 4
		addi $t2 $t2 4
		addi $t7 $t7 -1
		bne $t7 $0 subtractMainLoop
		jr $ra
		

#################################################
# TODO: float frobeneousNorm( float* A, int N )
# we assume pointer to A is in $a0 and N is in $a1
frobeneousNorm: 	
		move $t0 $a0
		mul $t7 $a1 $a1
		mtc1 $0 $f0
frobeneousNormMainLoop:
		lwc1 $f1 0($t0)
		mul.s $f2 $f1 $f1
		add.s $f0 $f0 $f2
		addi $t7 $t7 -1
		addi $t0 $t0 4
		bne $t7 $0 frobeneousNormMainLoop
		sqrt.s $f0 $f0
		jr $ra

#################################################
# TODO: void check ( float* C, float* D, int N )
# Print the forbeneous norm of the difference of C and D
# we assume $a0 is pointer to C and $a1 is pointer to D and $a2 is N
check: 		
		addi $sp $sp -12
		sw $ra 0($sp)
		sw $s0 4($sp)
		sw $s1 8($sp)
		move $s0 $a0
		move $s1 $a2
		move $a3 $a2
		move $a2 $a0
		jal subtract
		move $a0 $s0
		move $a1 $s1
		jal frobeneousNorm
		li $v0 2
		mov.s $f12 $f0
		syscall
		lw $ra 0($sp)
		lw $s0 4($sp)
		lw $s1 8($sp)
		addi $sp $sp 12
		jr $ra

##############################################################
# TODO: void MADD1( float*A, float* B, float* C, N )
# we assume pointer to A is in $a0 and pointer to B is in $a1
# and pointer to C is in $a2 and size N is in $a3
MADD1: 	
		li $t9 4
		mul $t9 $t9 $a3 #size of row in bytes
		mul $t8 $t9 $a3 #size of matrix in bytes
		move $t3 $a0
		move $t4 $a1
		move $t5 $a2
		move $t0 $a3 #i
MADD1RowLoop:
		addi $t0 $t0 -1 #inc i
		move $t1 $a3 #j
MADD1C1Loop:
		addi $t1 $t1 -1 #inc j
		move $t2 $a3 #k
MADD1C2Loop:
		addi $t2 $t2 -1 #inc k
		
		lwc1 $f1 0($t3)
		lwc1 $f2 0($t4)
		lwc1 $f3 0($t5)
		
		mul.s $f4 $f1 $f2
		add.s $f5 $f3 $f4
		
		add.s $f3 $f3 $f4
		
		swc1 $f3 0($t5)

		addi $t3 $t3 4
		add $t4 $t4 $t9
		bne $t2 $0 MADD1C2Loop
		beq $t2 $0 MADD1ResetC2Loop
MADD1Exit:
		jr $ra
MADD1ResetC1Loop:
		sub $t4 $t4 $t9 #go back to first column of B
		sub $t5 $t5 $t9 #go back to first column of C
		add $t3 $t3 $t9
		add $t5 $t5 $t9
		beq $t0 $0 MADD1Exit
		b MADD1RowLoop
MADD1ResetC2Loop:
		sub $t3 $t3 $t9 #go back to first column of A
		sub $t4 $t4 $t8 #go back to first row of B
		addi $t4 $t4 4
		addi $t5 $t5 4
		beq $t1 $0 MADD1ResetC1Loop
		b MADD1C1Loop






#########################################################
#instruction saving techniques: selectively unroll J and K loops, 
#pointers iteratively moved forwards and backwards with JJ,KK,I,J,K 
#and never computed by multiplication. Code should work most efficiently
#for N = m*bsize for some integer m but padding loops exist to handle cases
#where bsize does not divide N
# TODO: void MADD2( float*A, float* B, float* C, N )
MADD2: 
		addi $sp $sp -8
		sw $ra 0($sp)
		sw $s0 4($sp)
		li $s0 4 #bsize in elements
		mul $t8 $s0 4 #bsize in bytes
		mul $t7 $a3 4 #rowsize in bytes
		mul $t6 $t7 $s0 #bsize rows in bytes
		move $t9 $t7
		mul $t9 $t7 $a3 #arraysize in bytes
		move $t0 $0 #jj=0
MADD2JJLoop:
		bge $t0 $a3 MADD2EndJJLoop
		move $t1 $0 #kk=0
MADD2KKLoop:
		bge $t1 $a3 MADD2EndKKLoop
		move $t2 $0 #i=0
MADD2ILoop:
		beq $t2 $a3 MADD2EndILoop
		move $t3 $t0 #j=jj
		sub $t5 $a3 $s0#check if we can unroll j loop
		ble $t3 $t5 MADD2JLoopUnrolled
MADD2JLoop:
		beq $t3 $a3 MADD2EndJLoopAndPadPointers#if this branches we need to fix pointers
		add $t5 $t0 $s0
		beq $t3 $t5 MADD2EndJLoop
		lwc1 $f2 0($a2)
		move $t4 $t1 #k=kk
		sub $t5 $a3 $s0#check if we can unroll k loop
		ble $t4 $t5 MADD2KLoopUnrolled
		b MADD2KLoop
MADD2JLoopUnrolled:
		move $t4 $t1 #k=kk
		sub $t5 $a3 $s0#check if we can unroll k loop
		bgt $t4 $t5 MADD2JLoop#check if we cant unroll k loop
		lwc1 $f2 0($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		###########################
		#move pointers back with k
		sub $a1 $a1 $t6
		#add sum to c[i][j]
		swc1 $f2 0($a2)
		#########################################
		lwc1 $f2 4($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		###########################
		#move pointers back with k
		sub $a1 $a1 $t6
		#add sum to c[i][j]
		swc1 $f2 4($a2)
		#########################################
		lwc1 $f2 8($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		###########################
		#move pointers back with k
		sub $a1 $a1 $t6
		#add sum to c[i][j]
		swc1 $f2 8($a2)
		#########################################
		#mtc1 $0 $f2 #sum=0
		lwc1 $f2 12($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		###########################
		#move pointers back with k
		sub $a1 $a1 $t6
		#add sum to c[i][j]
		#lwc1 $f0 12($a2)
		#add.s $f0 $f0 $f2
		swc1 $f2 12($a2)
		#########################################
		#move pointers forward with j
		addi $a1 $a1 16
		addi $a2 $a2 16
		b MADD2EndJLoop
MADD2KLoop:
		beq $t4 $a3 MADD2EndKLoopAndPadPointers#if this branches need to fix pointers
		add $t5 $t1 $s0
		beq $t4 $t5 MADD2EndKLoop
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		addi $a0 $a0 4
		add $a1 $a1 $t7
		addi $t4 $t4 1 #k+= 1
		b MADD2KLoop
MADD2KLoopUnrolled:#for bsize = 4 this is a faster unrolled loop
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		addi $a0 $a0 16
		add $a1 $a1 $t7
		b MADD2EndKLoop
MADD2EndKLoopAndPadPointers:#only executed when bsize is not a factor of N
		add $t5 $t1 $s0
		beq $t4 $t5 MADD2EndKLoop
		addi $a0 $a0 4
		add $a1 $a1 $t7
		addi $t4 $t4 1
		b MADD2EndKLoopAndPadPointers
MADD2EndKLoop:
		#move pointers back with k
		sub $a0 $a0 $t8
		sub $a1 $a1 $t6
		#add sum to c[i][j]
		swc1 $f2 0($a2)
		#move pointers forward with j
		addi $a1 $a1 4
		addi $a2 $a2 4
		addi $t3 $t3 1 #j+=1
		b MADD2JLoop
MADD2EndJLoopAndPadPointers:
		add $t5 $t0 $s0
		beq $t3 $t5 MADD2EndJLoop
		addi $a1 $a1 4
		addi $a2 $a2 4
		addi $t3 $t3 1 #j+=1
		b MADD2EndJLoopAndPadPointers
MADD2EndJLoop:
		#move pointers backward with j
		sub $a1 $a1 $t8
		sub $a2 $a2 $t8
		#move pointers forward with i
		add $a0 $a0 $t7
		add $a2 $a2 $t7
		addi $t2 $t2 1 #i+=1
		b MADD2ILoop
MADD2EndILoop:
		#move pointers backward with i
		sub $a0 $a0 $t9
		sub $a2 $a2 $t9
		#move pointers forward with kk
		add $a0 $a0 $t8
		add $a1 $a1 $t6
		add $t1 $t1 $s0 #kk += bsize
		b MADD2KKLoop
MADD2EndKKLoop:
MADD2EndKKLoopDepadPointers:
		beq $t1 $a3 MADD2EndKKLoopDepaddedPointers
		sub $a0 $a0 4
		sub $a1 $a1 $t7
		addi $t1 $t1 -1
		b MADD2EndKKLoopDepadPointers
MADD2EndKKLoopDepaddedPointers:
		#move pointers backward with kk
		sub $a0 $a0 $t7
		sub $a1 $a1 $t9
		#move pointers forward with jj
		add $a1 $a1 $t8
		add $a2 $a2 $t8
		add $t0 $t0 $s0 #jj += bsize
		b MADD2JJLoop
MADD2EndJJLoop:
		lw $ra 0($sp)
		lw $s0 4($sp)
		addi $sp $sp 8
		jr $ra









MADD3: 
		addi $sp $sp -16
		sw $ra 0($sp)
		sw $s0 4($sp)
		sw $a0 8($sp)
		sw $a1 12($sp)
		move $a0 $a1
		move $a1 $a3
		jal Transpose
		move $a0 $a2
		jal Reverse
		mul $t5 $a1 $a1 #tmp to offset a2
		mul $t5 $t5 4
		add $a2 $a2 $t5
		lw $a0 8($sp)
		lw $a1 12($sp)
		li $s0 4 #bsize in elements
		mul $t8 $s0 4 #bsize in bytes
		mul $t7 $a3 4 #rowsize in bytes
		mul $t6 $t7 $s0 #bsize rows in bytes
		move $t9 $t7
		mul $t9 $t7 $a3 #arraysize in bytes
		move $t0 $0 #jj=0
MADD3JJLoop:
		bge $t0 $a3 MADD3EndJJLoop
		move $t1 $0 #kk=0
MADD3KKLoop:
		bge $t1 $a3 MADD3EndKKLoop
		move $t2 $0 #i=0
MADD3ILoop:
		beq $t2 $a3 MADD3EndILoop
		move $t3 $t0 #j=jj
		sub $t5 $a3 $s0#check if we can unroll j loop
		ble $t3 $t5 MADD3JLoopUnrolled
MADD3JLoop:
		beq $t3 $a3 MADD3EndJLoopAndPadPointers#if this branches we need to fix pointers
		add $t5 $t0 $s0
		beq $t3 $t5 MADD3EndJLoop
		lwc1 $f2 0($a2)
		move $t4 $t1 #k=kk
		sub $t5 $a3 $s0#check if we can unroll k loop
		ble $t4 $t5 MADD3KLoopUnrolled
		b MADD3KLoop
MADD3JLoopUnrolled:
		move $t4 $t1 #k=kk
		sub $t5 $a3 $s0#check if we can unroll k loop
		bgt $t4 $t5 MADD3JLoop#check if we cant unroll k loop
		lwc1 $f2 0($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove: (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		add $a1 $a1 $t7
		###########################
		#move pointers back with k
		#remove (-t8) sub $a1 $a1 $t6
		#add sum to c[i][j]
		swc1 $f2 0($a2)
		#########################################
		lwc1 $f2 -4($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		###########################
		#move pointers back with k
		#remove (-t8) sub $a1 $a1 $t6
		add $a1 $a1 $t7
		#add sum to c[i][j]
		swc1 $f2 -4($a2)
		#########################################
		lwc1 $f2 -8($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		###########################
		#move pointers back with k
		#remove (-t8) sub $a1 $a1 $t6
		add $a1 $a1 $t7
		#add sum to c[i][j]
		swc1 $f2 -8($a2)
		#########################################
		#mtc1 $0 $f2 #sum=0
		lwc1 $f2 -12($a2)
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		###########################
		#move pointers back with k
		#remove (-t8) sub $a1 $a1 $t6
		add $a1 $a1 $t7
		#add sum to c[i][j]
		#lwc1 $f0 12($a2)
		#add.s $f0 $f0 $f2
		swc1 $f2 -12($a2)
		#########################################
		#move pointers forward with j
		#remove (+t6) addi $a1 $a1 16
		addi $a2 $a2 -16
		b MADD3EndJLoop
MADD3KLoop:
		beq $t4 $a3 MADD3EndKLoopAndPadPointers#if this branches need to fix pointers
		add $t5 $t1 $s0
		beq $t4 $t5 MADD3EndKLoop
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		addi $a0 $a0 4
		addi $a1 $a1 4
		addi $t4 $t4 1 #k+= 1
		b MADD3KLoop
MADD3KLoopUnrolled:#for bsize = 4 this is a faster unrolled loop
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 0($a0)
		lwc1 $f1 0($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 4($a0)
		lwc1 $f1 4($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 8($a0)
		lwc1 $f1 8($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		#remove (+4) add $a1 $a1 $t7
		#add A[i][k]*B[k][j] to sum
		lwc1 $f0 12($a0)
		lwc1 $f1 12($a1)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		#move pointers forward with k
		addi $a0 $a0 16
		addi $a1 $a1 16
		#remove (+4) add $a1 $a1 $t7
		b MADD3EndKLoop
MADD3EndKLoopAndPadPointers:#only executed when bsize is not a factor of N
		add $t5 $t1 $s0
		beq $t4 $t5 MADD3EndKLoop
		addi $a0 $a0 4
		addi $a1 $a1 4
		addi $t4 $t4 1
		b MADD3EndKLoopAndPadPointers
MADD3EndKLoop:
		#move pointers back with k
		sub $a0 $a0 $t8
		sub $a1 $a1 $t8
		#add sum to c[i][j]
		swc1 $f2 0($a2)
		#move pointers forward with j
		add $a1 $a1 $t7
		addi $a2 $a2 -4
		addi $t3 $t3 1 #j+=1
		b MADD3JLoop
MADD3EndJLoopAndPadPointers:
		add $t5 $t0 $s0
		beq $t3 $t5 MADD3EndJLoop
		add $a1 $a1 $t7
		addi $a2 $a2 -4
		addi $t3 $t3 1 #j+=1
		b MADD3EndJLoopAndPadPointers
MADD3EndJLoop:
		#move pointers backward with j
		sub $a1 $a1 $t6
		add $a2 $a2 $t8
		#move pointers forward with i
		add $a0 $a0 $t7
		sub $a2 $a2 $t7
		addi $t2 $t2 1 #i+=1
		b MADD3ILoop
MADD3EndILoop:
		#move pointers backward with i
		sub $a0 $a0 $t9
		add $a2 $a2 $t9
		#move pointers forward with kk
		add $a0 $a0 $t8
		add $a1 $a1 $t8
		add $t1 $t1 $s0 #kk += bsize
		b MADD3KKLoop
MADD3EndKKLoop:
MADD3EndKKLoopDepadPointers:
		beq $t1 $a3 MADD3EndKKLoopDepaddedPointers
		sub $a0 $a0 4
		sub $a1 $a1 4
		addi $t1 $t1 -1
		b MADD3EndKKLoopDepadPointers
MADD3EndKKLoopDepaddedPointers:
		#move pointers backward with kk
		sub $a0 $a0 $t7
		sub $a1 $a1 $t7
		#move pointers forward with jj
		add $a1 $a1 $t6
		sub $a2 $a2 $t8
		add $t0 $t0 $s0 #jj += bsize
		b MADD3JJLoop
MADD3EndJJLoop:
		move $a0 $a2
		mul $t5 $a3 $a3
		mul $t5 $t5 4
		sub $a0 $a0 $t5
		move $a1 $a3
		jal Reverse
		lw $ra 0($sp)
		lw $s0 4($sp)
		addi $sp $sp 16
		jr $ra
		
#################################
#//transposing the B matrix pre MADD results in performance increase 
#//for 4-way cache and performance decrease for 8-way and direct
#accept pointer a0 to matrix and transpose
#N - a1
Transpose:
		mul $t5 $a1 4 #size of row in bytes
		mul $t6 $t5 $a1 #size of array in bytes
		li $t2 4 #x offset for true pointer
		move $t3 $t5 #y offset for transpose pointer
		move $t0 $0 #i=0
		move $t9 $a0 #transpose pointer
TransposeILoop:
		beq $t0 $a1 TransposeEndILoop
		addi $t1 $t0 1 #j=i+1
TransposeJLoop:
		beq $t1 $a3 TransposeEndJLoop
		#move pointers forward with j
		addi $a0 $a0 4 #right
		add $t9 $t9 $t5 #down
		lw $t7 0($a0)
		lw $t8 0($t9)
		sw $t7 0($t9)
		sw $t8 0($a0)
		addi $t1 $t1 1 #j+=1
		b TransposeJLoop
TransposeEndJLoop:
		#move pointers backward with j
		addi $t2 $t2 4 #increase x offset
		sub $a0 $a0 $t5
		add $a0 $a0 $t2
		add $t3 $t3 $t5 #increase y offset
		sub $t9 $t9 $t6
		add $t9 $t9 $t3
		#move pointers forward with i
		add $a0 $a0 $t5
		addi $t9 $t9 4
		addi $t0 $t0 1 #i+=1
		b TransposeILoop
TransposeEndILoop:
		jr $ra

#################################
#accept pointer a0 to matrix and reverse
#N - a1		
Reverse:
		mul $t2 $a3 $a3
		mul $t2 $t2 4
		move $t0 $a0
		add $t1 $a0 $t2
ReverseLoop:
		bge $t0 $t1 EndReverseLoop
		lw $t3 0($t0)
		lw $t4 0($t1)
		sw $t3 0($t1)
		sw $t4 0($t0)
		addi $t0 $t0 4
		addi $t1 $t1 -4
		b ReverseLoop
EndReverseLoop:
		jr $ra



