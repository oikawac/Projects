while (True):
	quit = input("quit/continue? (q/any key)...")
	if quit == "q":
		break
	bitstring1 = input(" first pos: ")
	bitstring2 = input("second pos: ")
	bitstring1 = bitstring1.replace(' ', '')
	bitstring2 = bitstring2.replace(' ', '')
	bit = 0
	for q in range(4):
		for r in range(3):
			for c in range(3):
				char = bitstring1[0-bit]
				print(char)
				bit += 1
	print(bitstring1)
	print(bitstring2)
