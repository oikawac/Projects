
int main() {
    int i0;
    int i1;
    int i2;
    int i3;
    int i4;
    i0 = 0;
    i1 = 1;
    i2 = 2;
    i3 = 3;
    i4 = 4;
    print_s((char*)" 0 ");
    print_i(i0);
    print_s((char*)" 1 ");
    print_i(i0+i1);
    print_s((char*)" -1 ");
    print_i(i2-i3);
    print_s((char*)" 0 ");
    print_i(i4*i0);
    print_s((char*)" 2 ");
    print_i(i4/i2);
    print_s((char*)" 0 ");
    print_i(i4%i2);
    print_s((char*)" 1 ");
    print_i(i3%i2);
    print_s((char*)" -2 ");
    print_i(i1+i2*i3-i4/i3+i1%i1+i2-i3-9+i3/3+(i1+i2)/i3);
}
