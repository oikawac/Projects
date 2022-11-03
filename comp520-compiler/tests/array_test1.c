
int main() {
    int a[10];
    int b[10];
    int j;
    int k;
    int i;
    j = 0;
    while (j < 10) {
        a[j] = j;
        b[j] = j;
        j = j + 1;
    }
    k = 0;
    while (k < 25) {
        a[k % 10] = b[(k+1) % 10]+1;
        b[k % 10] = a[(k*7) % 10]+1;
        k = k + 1;
    }
    i = 0;
    j = 0;
    while (j < 10) {
        i = i + a[j] + b[j];
        j = j + 1;
    }
    print_i(i);
}