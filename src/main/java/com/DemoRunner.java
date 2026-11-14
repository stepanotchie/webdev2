package com.week9.activity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManagerFactory;

@Component
public class DemoRunner implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    VaccineRepository vaccineRepository;
    @Autowired
    VaccinationRecordRepository vaccinationRecordRepository;
    @Autowired
    EmployeeService employeeService;

    private final TransactionTemplate tx;
    private final Statistics stats;

    public DemoRunner(PlatformTransactionManager transactionManager, EntityManagerFactory emf) {
        this.tx = new TransactionTemplate(transactionManager);
        this.stats = emf.unwrap(SessionFactory.class).getStatistics();
    }

    @Override
    public void run(String... args) {
        task1();
        task2();
        task3();
        task4();
        task5();
        banner("ALL TASKS FINISHED");
    }

    private void task1() {
        banner("TASK 1 - One-to-One + One-to-Many");

        User user = new User("steph");
        user.setProfile(new UserProfile("Loves sewing and baking"));
        User savedUser = userRepository.save(user);
        tx.executeWithoutResult(s -> {
            User u = userRepository.findById(savedUser.getId()).orElseThrow();
            System.out.println("[1:1] " + u.getUsername() + " <-> profile '" + u.getProfile().getBio()
                    + "' (profile.user = " + u.getProfile().getUser().getUsername() + ")");
        });

        Department eng = new Department("Engineering");
        eng.addEmployee(new Employee("Maryx"));
        eng.addEmployee(new Employee("Eli"));
        eng.addEmployee(new Employee("Hannah"));
        departmentRepository.save(eng);

        tx.executeWithoutResult(s -> departmentRepository.findAll().forEach(
                d -> System.out.println("[1:N] findAll(): " + d.getName() + " -> " + names(d.getEmployees()))));
        System.out.println("[1:N] employee rows = " + employeeRepository.count()
                + " (each row has department_id set by the addEmployee() helper)");
    }

    private void task2() {
        banner("TASK 2 - Association entity Enrollment + unique constraint");

        Student jan = studentRepository.save(new Student("Jan liam"));
        Course webdev = courseRepository.save(new Course("WEBDEV2"));

        Enrollment first = enroll(jan.getId(), webdev.getId(), "A");
        System.out.println("[2] ENROLL OK: enrollment id=" + first.getId() + ", grade=" + first.getGrade()
                + ", date=" + first.getEnrollmentDate());
        try {
            enroll(jan.getId(), webdev.getId(), "B");
            System.out.println("[2] ERROR: duplicate was accepted!");
        } catch (DataIntegrityViolationException ex) {
            System.out.println("[2] DUPLICATE REJECTED by uk_student_course: "
                    + NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
        System.out.println("[2] enrollment rows = " + enrollmentRepository.count());
    }

    private void task3() {
        banner("TASK 3 - LAZY: exception, then two fixes");

        Department research = new Department("Marketing");
        research.addEmployee(new Employee("Geoff"));
        departmentRepository.save(research);
        Long employeeId = employeeRepository.findAll().stream()
                .filter(e -> "Geoff".equals(e.getName())).findFirst().orElseThrow().getId();

        stats.clear();
        try {
            Employee e = employeeService.getEmployee(employeeId);
            System.out.println(e.getDepartment().getName());
            System.out.println("[A] ERROR: expected LazyInitializationException");
        } catch (LazyInitializationException ex) {
            System.out.println("[A] LAZY, no transaction -> " + ex.getClass().getName() + ": " + ex.getMessage());
            StackTraceElement[] trace = ex.getStackTrace();
            for (int i = 0; i < Math.min(4, trace.length); i++) {
                System.out.println("        at " + trace[i]);
            }
        }
        System.out.println("[A] SQL statements = " + stats.getPrepareStatementCount()
                + "  (employee SELECT only, the department was never loaded)");

        stats.clear();
        String name = employeeService.getDepartmentName(employeeId);
        System.out.println("[B] @Transactional -> department = " + name + " | SQL statements = "
                + stats.getPrepareStatementCount() + "  (employee SELECT + department SELECT)");

        stats.clear();
        String name2 = employeeService.getEmployeeWithDepartment(employeeId).getDepartment().getName();
        System.out.println("[C] JOIN FETCH -> department = " + name2 + " | SQL statements = "
                + stats.getPrepareStatementCount() + "  (one SELECT with a join)");
    }

    private void task4() {
        banner("TASK 4 - Cascade + orphanRemoval: safe vs dangerous");

        Department support = new Department("Customer Support");
        support.addEmployee(new Employee("Carlo Valdez"));
        support.addEmployee(new Employee("Elizabeth Parchamento"));
        Department savedSupport = departmentRepository.save(support);
        System.out.println("[4a] BEFORE: employee table = " + employeeNames());
        tx.executeWithoutResult(s -> {
            Department d = departmentRepository.findById(savedSupport.getId()).orElseThrow();
            Employee carlo = d.getEmployees().stream().filter(e -> "Carlo Valdez".equals(e.getName())).findFirst()
                    .orElseThrow();
            d.removeEmployee(carlo);
            departmentRepository.save(d);
        });
        System.out.println("[4a] AFTER removeEmployee(Carlo) + save: employee table = " + employeeNames()
                + "  -> Carlo's row was DELETED");

        Student mary = studentRepository.save(new Student("Mary"));
        Student bob = studentRepository.save(new Student("Bob"));
        Course shared = courseRepository.save(new Course("Shared Databases"));
        enroll(mary.getId(), shared.getId(), "A");
        enroll(bob.getId(), shared.getId(), "B");
        System.out.println("[4b] BEFORE deleting Mary: students=" + studentRepository.count()
                + ", courses=" + courseRepository.count() + ", enrollments=" + enrollmentRepository.count());
        tx.executeWithoutResult(s -> studentRepository.delete(studentRepository.findById(mary.getId()).orElseThrow()));
        System.out.println("[4b] AFTER  deleting Mary: students=" + studentRepository.count()
                + ", courses=" + courseRepository.count() + ", enrollments=" + enrollmentRepository.count()
                + ", shared course still exists = " + courseRepository.existsById(shared.getId())
                + "  -> only Mary's enrollment was removed");
    }

    private void task5() {
        banner("TASK 5 - Capstone smoke test (AppUser 1-N Pet, Pet N-M Vaccine via VaccinationRecord)");

        long[] ids = tx.execute(s -> {
            Vaccine rabies = vaccineRepository.save(new Vaccine("Rabies"));
            Vaccine distemper = vaccineRepository.save(new Vaccine("Distemper"));
            AppUser user = new AppUser("stephy-capstone", "stephy@example.com");
            Pet max = new Pet("Max", "Dog", "MC-001");
            Pet luna = new Pet("Luna", "Cat", "MC-002");
            user.addPet(max);
            user.addPet(luna);
            max.vaccinate(rabies, LocalDate.of(2026, 1, 10), 1);
            max.vaccinate(distemper, LocalDate.of(2026, 2, 14), 1);
            luna.vaccinate(rabies, LocalDate.of(2026, 3, 1), 1);
            appUserRepository.save(user);
            return new long[] { user.getId(), max.getId(), rabies.getId() };
        });

        long users = appUserRepository.count();
        long pets = petRepository.count();
        long vaccines = vaccineRepository.count();
        long records = vaccinationRecordRepository.count();
        System.out.println("[5] rows: app_user=" + users + ", pet=" + pets
                + ", vaccine=" + vaccines + ", vaccination_record=" + records);
        check(users == 1 && pets == 2 && vaccines == 2 && records == 3, "unexpected row counts");

        tx.executeWithoutResult(s -> {
            AppUser u = appUserRepository.findById(ids[0]).orElseThrow();
            check(u.getPets().size() == 2, "expected 2 pets for the user");
            for (Pet p : u.getPets()) {
                List<String> shots = new ArrayList<>();
                for (VaccinationRecord r : p.getVaccinationRecords()) {
                    shots.add(r.getVaccine().getName() + " dose " + r.getDoseNumber());
                }
                int expected = "Max".equals(p.getName()) ? 2 : 1;
                check(shots.size() == expected, p.getName() + " should have " + expected + " record(s)");
                System.out.println("[5] " + u.getUsername() + " owns " + p.getName() + " (" + p.getSpecies()
                        + ") -> " + shots);
            }
        });

        boolean rejected = false;
        try {
            tx.executeWithoutResult(s -> {
                Pet max = petRepository.findById(ids[1]).orElseThrow();
                Vaccine rabies = vaccineRepository.findById(ids[2]).orElseThrow();
                vaccinationRecordRepository.saveAndFlush(max.vaccinate(rabies, LocalDate.now(), 1));
            });
        } catch (DataIntegrityViolationException ex) {
            rejected = true;
            System.out.println("[5] Duplicate REJECTED by uk_pet_vaccine_dose: "
                    + NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
        check(rejected, "duplicate vaccination was accepted");
        System.out.println("[5] SMOKE TEST PASSED");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException("[5] SMOKE TEST FAILED: " + message);
        }
    }

    private Enrollment enroll(Long studentId, Long courseId, String grade) {
        return tx.execute(s -> {
            Student student = studentRepository.findById(studentId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();
            Enrollment e = student.enroll(course, LocalDate.now(), grade);
            return enrollmentRepository.saveAndFlush(e);
        });
    }

    private static List<String> names(List<Employee> employees) {
        List<String> out = new ArrayList<>();
        for (Employee e : employees) {
            out.add(e.getName());
        }
        return out;
    }

    private List<String> employeeNames() {
        return names(employeeRepository.findAll());
    }

    private static void banner(String text) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("  " + text);
        System.out.println("==================================================================");
    }
}
