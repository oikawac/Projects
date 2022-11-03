struct inner {
    int d;
    int e[3];
    int* f;
};
struct test {
    char a;
    int b;
    int c[10];
    struct inner i;
};
struct test2 {
    int i;
    char c;
    int k[10];
};

int main() {
    struct inner in;
    struct test t;
    struct test2 t2[10];
    in.d = 9;
    in.e[0] = 9;
    in.f = &in.e[0];
    t.i = in;
    t.a = '9';
    t.b = *t.i.f;
    t.i.f = &t.b;
    t.c[0] = *&t.i.e[0];
    in.d = 11;
    in.e[0] = 11;
    in.f = &in.d;
    t2[0].k[0] = 1;
    t2[1].k[1] = t2[0].k[0];
    t2[2] = t2[1];
    t2[0].k[0] = 0;
    t2[1].k[1] = 0;
    if (t2[2].k[1]+t2[0].k[0]+t2[1].k[1] != 1) {
        print_i(0);//fail
    } else {
        print_i(1);//pass
    }
    if (t.i.d+t.i.e[0]+*t.i.f+t.b+t.c[0] != 9*5) {
        print_i(0);//fail
    } else {
        print_i(1);//pass
    }
    if (t.a != '9') {
        print_i(0);//fail
    } else {
        print_i(1);//pass
    }
}