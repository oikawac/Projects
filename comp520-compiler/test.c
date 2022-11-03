
class Course {
    char name[20];
    int credit;
    int courseWorkScore;

    void whereToAttend() {
        print_s((char*)"Not determined! The course will be held virtually or in person!\n");
    }
    int hasExam() {
        return 89;
    }
}

class VirtualCourse extends Course {
    char zoomLink[200];
    void whereToAttend() {
        print_s((char*)"The course is going to be held on Zoom!\n");
    }
}

class VirtualCourseOnFire extends VirtualCourse {
    int credit;
    void whereToAttend() {
        print_s((char*)"The course is going to be held on Zoom! On Fire!\n");
    }
}

int main() {
    class Course course;
    class VirtualCourse vcourse;
    class VirtualCourseOnFire vcourseonfire;
    course = new class Course();
    vcourse = new class VirtualCourse();
    vcourseonfire = new class VirtualCourseOnFire();

    course.whereToAttend();
    vcourse.whereToAttend();
    vcourseonfire.whereToAttend();
    vcourseonfire.zoomLink[10] = 'a';
    vcourseonfire.temperature = 10;
    vcourseonfire.credit = 123;

    return 0;

}