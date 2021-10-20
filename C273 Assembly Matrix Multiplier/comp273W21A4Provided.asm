# TODO: CAILEAN OIKAWA 260836366
# TODO: ADD OTHER COMMENTS YOU HAVE HERE AT THE TOP OF THIS FILE
# TODO: SEE LABELS FOR PROCEDURES YOU MUST IMPLEMENT AT THE BOTTOM OF THIS FILE

.data
TestNumber:	.word 1		# TODO: Which test to run!
				# 0 compare matrices stored in files Afname and Bfname
				# 1 test Proc using files A through D named below
				# 2 compare MADD1 and MADD2 with random matrices of size Size
				
Proc:		MADD2		# Procedure used by test 2, set to MADD1 or MADD2		
				
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
		
		b Debug2
		EndDebug2:
		
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
# TODO: void MADD2( float*A, float* B, float* C, N )
MADD2: 
		li $t0 16 #bsize
		li $t9 4
		mul $t9 $t9 $a3 #size in bytes
		move $t1 $0 #jj=0
MADD2JJLoop:
		bge $t1 $a3 MADD2EndJJLoop
		move $t2 $0 #kk=0
MADD2KKLoop:
		bge $t2 $a3 MADD2EndKKLoop
		move $t3 $0 #i=0
MADD2ILoop:
		bge $t3 $a3 MADD2EndILoop
		move $t4 $t1 #j=jj
MADD2JLoop:
		bge $t4 $a3 MADD2EndJLoop
		add $t6 $t1 $t0
		bge $t4 $t6 MADD2EndJLoop
		mtc1 $0 $f2 #sum=0
		move $t5 $t2 #k=kk
MADD2KLoop:
		bge $t5 $a3 MADD2EndKLoop
		add $t6 $t2 $t0
		bge $t5 $t6 MADD2EndKLoop
		mul $t7 $t3 $t9 #Aoffset = size*i*4
		li $t8 4
		mul $t8 $t8 $t5 #k*4
		add $t7 $t7 $t8 #Aoffset += k*4
		add $t7 $t7 $a0 #Aaddress += Aoffset
		lwc1 $f0 0($t7)
		mul $t7 $t5 $t9 #Boffset = size*k*4
		li $t8 4
		mul $t8 $t8 $t4 #j*4
		add $t7 $t7 $t8 #Boffset += j
		add $t7 $t7 $a1 #Baddress += Boffset
		lwc1 $f1 0($t7)
		mul.s $f1 $f0 $f1 #prod = A[i][k] * B[k][j]
		add.s $f2 $f2 $f1 #sum += prod
		addi $t5 $t5 1 #k+= 1
		b MADD2KLoop
MADD2EndKLoop:
		mul $t7 $t3 $t9 #Coffset = size*i*4
		li $t8 4
		mul $t8 $t8 $t4 #j*4
		add $t7 $t7 $t8 #Coffset += j*4
		add $t7 $t7 $a2 #Caddress += Coffset
		lwc1 $f0 0($t7)
		add.s $f0 $f0 $f2
		swc1 $f0 0($t7)
		addi $t4 $t4 1 #j+=1
		b MADD2JLoop
MADD2EndJLoop:
		addi $t3 $t3 1 #i+=1
		b MADD2ILoop
MADD2EndILoop:
		add $t2 $t2 $t0 #kk += bsize
		b MADD2KKLoop
MADD2EndKKLoop:
		add $t1 $t1 $t0 #kk += bsize
		b MADD2JJLoop
MADD2EndJJLoop:
		jr $ra


Debug:
	addi $sp $sp -4
	sw $a0 0($sp)
	li $v0 1
	move $a0 $t1
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $v0 1
	move $a0 $t2
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $v0 1
	move $a0 $t3
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $v0 1
	move $a0 $t4
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $v0 1
	move $a0 $t5
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $a0 10
	syscall
	lw $a0 0($sp)
	addi $sp $sp 4
	b EndDebug

Debug2:
	addi $sp $sp -4
	sw $a0 0($sp)
	li $v0 1
	move $a0 $t0
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $v0 1
	move $a0 $t1
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $v0 1
	move $a0 $t2
	syscall
	li $v0 11
	li $a0 32
	syscall
	li $a0 10
	syscall
	lw $a0 0($sp)
	addi $sp $sp 4
	b EndDebug2
	EndDebug:


