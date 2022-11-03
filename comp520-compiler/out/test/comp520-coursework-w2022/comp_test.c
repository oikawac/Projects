
int main() {
    int i;
    int j;
    int k;
    i = 1;
    j = 10;
    k = 0;
    if (i > j) {
        k = k + 1;
    } else {
        k = k + 2;
    }
    if (i < j) {
        k = k + 4;
    } else {
        k = k + 8;
    }
    if (i >= j) {
        k = k + 16;
    } else {
        k = k + 32;
    }
    if (i <= j) {
        k = k + 64;
    } else {
        k = k + 128;
    }
    if (i > i) {
        k = k + 256;
    } else {
        k = k + 512;
    }
    if (i < i) {
        k = k + 1024;
    } else {
        k = k + 2048;
    }
    if (i >= i) {
        k = k + 4096;
    } else {
        k = k + 8192;
    }
    if (i <= i) {
        k = k + 16384;
    } else {
        k = k + 16384*2;
    }
    if (i == j) {
        k = k + 16384*4;
    } else {
        k = k + 16384*8;
    }
    if (i != j) {
        k = k + 16384*4;
    } else {
        k = k + 16384*8;
    }
    if (i == i) {
        k = k + 16384*16;
    } else {
        k = k + 16384*32;
    }
    if (i != i) {
        k = k + 16384*64;
    } else {
        k = k + 16384*128;
    }
    print_i(k);
    //printf("%d\n",k);
}