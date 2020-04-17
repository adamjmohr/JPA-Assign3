/***************************************************************************f******************u************zz*******y**
 * File: EmployeeTestSuite.java
 * Course materials (20W) CST 8277
 * @author (original) Mike Norman
 * @author Adam Mohr 040669681
 *
 */
package com.algonquincollege.cst8277.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.algonquincollege.cst8277.TestSuiteBase;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Subclass for the actual tests to be conducted for Assignment 3.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeSystemTestSuite extends TestSuiteBase {
    /** class name */
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    /** log messages */
    private static final Logger logger = LoggerFactory.getLogger(_thisClaz);
    /** eclipse link logger */
    private static final ch.qos.logback.classic.Logger eclipselinkSqlLogger =
        (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ECLIPSELINK_LOGGING_SQL);
    /** basic sql select statement to test if setup works */
    private static final String SELECT_EMPLOYEE_1 =
        "SELECT EMP_ID, CREATED_DATE, EMAIL, FNAME, LNAME, SALARY, TITLE, UPDATED_DATE, VERSION, ADDR_ID FROM EMPLOYEE WHERE (EMP_ID = ?)";
    
    /**
     * Test setup works and captured sql.
     */
    @Test
    public void test01_setup() {
        logger.info("first test case");
        EntityManager em = emf.createEntityManager();

        ListAppender<ILoggingEvent> listAppender = attachListAppender(eclipselinkSqlLogger, ECLIPSELINK_LOGGING_SQL);
        EmployeePojo emp1 = em.find(EmployeePojo.class, 1);
        detachListAppender(eclipselinkSqlLogger, listAppender);

        assertNull(emp1);
        List<ILoggingEvent> loggingEvents = listAppender.list;
        assertEquals(1, loggingEvents.size());
        assertThat(loggingEvents.get(0).getMessage(), startsWith(SELECT_EMPLOYEE_1));
        em.close();
    }

    /**
     * Test creating new employee works.
     */
    @Test
    public void test02_new_employee() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // Create a new employee
        EmployeePojo emp = new EmployeePojo();
        emp.setFirstName("Adam");
        emp.setLastName("Mohr");
        emp.setEmail("adam@gmail.com");
        em.persist(emp);
        tx.commit();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo result = q1.getSingleResult();
        assertEquals(result.getFirstName(), "Adam");
        assertEquals(result.getLastName(), "Mohr");
        assertEquals(result.getEmail(), "adam@gmail.com");
        emp.setSalary(200001d);
        em.close();
    }

    /**
     * Test for finding expensive employee salaries.
     */
    @Test
    public void test03_expensive_employees() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // Create new employees
        EmployeePojo emp1 = new EmployeePojo();
        emp1.setSalary(200000d);
        em.persist(emp1);
        
        EmployeePojo emp2 = new EmployeePojo();
        emp2.setFirstName("James");
        emp2.setLastName("Mohr");
        emp2.setEmail("james@gmail.com");
        emp2.setSalary(200001d);
        em.persist(emp2);
        tx.commit();

        // all 'expensive' Employees
        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.salary > 200000",
                EmployeePojo.class);
        List<EmployeePojo> result = q1.getResultList();
        assertEquals(1, result.size());
        em.close();
    }

    /**
     * Test for how many employees there are.
     */
    @Test
    public void test04_employee_count() {
        EntityManager em = emf.createEntityManager();

        // how may Employees are there?
        TypedQuery<Long> countQuery = em.createQuery("select count(e) from Employee e", Long.class);
        long numEmployees = countQuery.getSingleResult();
        
        // 3 employees created so far in tests
        assertEquals(3, numEmployees);
        em.close();
    }

    /**
     * Test to find employees not working on any projects.
     */
    @Test
    public void test05_employees_no_projects() {
        EntityManager em = emf.createEntityManager();

        // find Employees that are not actually working on any Projects
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.projects is empty",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();
        
        // 3 employees created and not assigned to any projects thus far
        assertEquals(3, result.size());
        em.close();
    }

    /**
     * Test to find employees with last name starting with 'M' and using Criteria query.
     */
    @Test
    public void test06_employees_last_name() {
        EntityManager em = emf.createEntityManager();

        // Employees whose lastname starts with M
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);
        
        // make sure we do not spell ‘lastname’ wrong
        query.where(queryBuilder.like(queryBuilder.upper(root.get(EmployeePojo_.lastName)), "M%"));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();
        
        // 2 employees created with last name starting with 'M' thus far
        assertEquals(2, result2.size());
        em.close();
    }

    /**
     * Test assigning employee tasks.
     */
    @Test
    public void test07_employee_tasks() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo result = q1.getSingleResult();

        // assign tasks
        List<EmployeeTask> tasks = new ArrayList<>();
        EmployeeTask task1 = new EmployeeTask();
        task1.setDescription("task 1");

        EmployeeTask task2 = new EmployeeTask();
        task2.setDescription("task 2");

        tasks.add(task1);
        tasks.add(task2);

        result.setTasks(tasks);
        
        em.merge(result);
        tx.commit();
        
        assertEquals(2, result.getTasks().size());
        em.close();
    }

    /**
     * Test removing tasks assigned to employee.
     */
    @Test
    public void test08_remove_tasks() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo result = q1.getSingleResult();
        
        // remove tasks previously assigned
        result.setTasks(null);
        
        em.merge(result);
        tx.commit();
        
        assertEquals(0, result.getTasks().size());
        em.close();
    }

    /**
     * Test creating new project.
     */
    @Test
    public void test09_new_project() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        ProjectPojo proj1 = new ProjectPojo();
        proj1.setName("proj 1");
        
        em.persist(proj1);
        tx.commit();
        
        TypedQuery<ProjectPojo> q1 = em.createQuery("select p from Project p where p.name = 'proj 1'",
                ProjectPojo.class);
        ProjectPojo result = q1.getSingleResult();
        
        assertEquals(result.getName(), "proj 1");
        em.close();
    }

    /**
     * Test assigning project.
     */
    @Test
    public void test10_assign_project() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        // find Employees that are not actually working on any Projects
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.projects is empty",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();
        
        // assign project to all employees without one
        ProjectPojo project = em.find(ProjectPojo.class, 1);
        project.setEmployees(result);
        tx.commit();
        
        assertEquals(3, project.getEmployees().size());
        em.close();
    }

    /**
     * Test removing a project.
     */
    @Test
    public void test11_remove_project() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // remove project
        ProjectPojo project = em.find(ProjectPojo.class, 1);
        em.remove(project);
        tx.commit();

        // should be zero now
        TypedQuery<ProjectPojo> query = em.createQuery("select p from Project p", ProjectPojo.class);
        List<ProjectPojo> projects = query.getResultList();
        assertEquals(0, projects.size());
        em.close();
    }

    /**
     * Test no phones for employees to start.
     */
    @Test
    public void test12_no_phones() {
        EntityManager em = emf.createEntityManager();

        // find Employees that have phones
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.phones is not empty",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();

        // should be zero to start
        assertEquals(0, result.size());
        em.close();
    }

    /**
     * Test creating a new mobile phone.
     */
    @Test
    public void test13_new_mobile_phone() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        MobilePhone phone = new MobilePhone();
        phone.setPhoneNumber("599-4281");
        phone.setProvider("Bell");
        em.persist(phone);
        tx.commit();
        
        TypedQuery<MobilePhone> query = em.createQuery("select p from Phone p where p.phoneNumber = '599-4281'",
                MobilePhone.class);
        MobilePhone result = query.getSingleResult();
        assertEquals(result.getPhoneNumber(), "599-4281");
        assertEquals(result.getProvider(), "Bell");
        em.close();
    }

    /**
     * Test creating a new home phone.
     */
    @Test
    public void test14_new_home_phone() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        HomePhone phone = new HomePhone();
        phone.setPhoneNumber("432-2962");
        phone.setDirections("Left off Kanata Ave");
        em.persist(phone);
        tx.commit();
        
        TypedQuery<HomePhone> query = em.createQuery("select p from Phone p where p.phoneNumber = '432-2962'",
                HomePhone.class);
        HomePhone result = query.getSingleResult();
        assertEquals(result.getPhoneNumber(), "432-2962");
        assertEquals(result.getDirections(), "Left off Kanata Ave");
        em.close();
    }

    /**
     * Test creating a new work phone.
     */
    @Test
    public void test15_new_work_phone() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        WorkPhone phone = new WorkPhone();
        phone.setPhoneNumber("514-4200");
        phone.setDepartment("IT");
        em.persist(phone);
        tx.commit();
        
        TypedQuery<WorkPhone> query = em.createQuery("select p from Phone p where p.phoneNumber = '514-4200'",
                WorkPhone.class);
        WorkPhone result = query.getSingleResult();
        assertEquals(result.getPhoneNumber(), "514-4200");
        assertEquals(result.getDepartment(), "IT");
        em.close();
    }

    /**
     * Test assigning phones to employee.
     */
    @Test
    public void test16_assigning_phones() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        TypedQuery<WorkPhone> query = em.createQuery("select p from Phone p where p.phoneNumber = '514-4200'",
                WorkPhone.class);
        WorkPhone workPhone = query.getSingleResult();
        
        TypedQuery<MobilePhone> query2 = em.createQuery("select p from Phone p where p.phoneNumber = '599-4281'",
                MobilePhone.class);
        MobilePhone mobilePhone = query2.getSingleResult();
        
        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo emp = q1.getSingleResult();
        emp.addPhone(workPhone);
        emp.addPhone(mobilePhone);
        
        em.persist(emp);
        tx.commit();
        
        assertEquals(2, emp.getPhones().size());
        em.close();
    }

    /**
     * Test removing phones from employee.
     */
    @Test
    public void test17_remove_phones() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo emp = q1.getSingleResult();
        emp.setPhones(null);
        
        em.merge(emp);
        tx.commit();
        
        assertEquals(0, emp.getPhones().size());
        em.close();
    }

    /**
     * Test removing an employee.
     */
    @Test
    public void test18_remove_emp() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // find all employees, should be three at this point
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e", EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();
        assertEquals(3, result.size());
        
        // remove third employee
        result.remove(2);
        tx.commit();
        
        // count should be 2 now
        assertEquals(2, result.size());
        em.close();
    }

    /**
     * Test adding a new address.
     */
    @Test
    public void test19_new_address() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        EmployeePojo emp = new EmployeePojo();
        emp.setFirstName("Alex");

        AddressPojo address = new AddressPojo();
        address.setCity("Ottawa");

        emp.setAddress(address);
        em.merge(emp);
        tx.commit();

        TypedQuery<AddressPojo> q2 = em.createQuery("select a from Address a", AddressPojo.class);
        AddressPojo result = q2.getSingleResult();
        assertEquals("Ottawa", result.getCity());
        em.close();
    }

    /**
     * Test updating an address with cascade update.
     */
    @Test
    public void test20_update_address_cascade() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.firstName = 'Alex'",
                EmployeePojo.class);
        EmployeePojo emp = q1.getSingleResult();
        emp.getAddress().setCity("Kanata");

        em.merge(emp);
        tx.commit();

        TypedQuery<AddressPojo> q2 = em.createQuery("select a from Address a", AddressPojo.class);
        AddressPojo result = q2.getSingleResult();
        assertEquals("Kanata", result.getCity());
        em.close();
    }

    /**
     * Test removing an address with cascade delete.
     */
    @Test
    public void test21_remove_address_cascade() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.firstName = 'Alex'",
                EmployeePojo.class);
        EmployeePojo emp = q1.getSingleResult();
        emp.setAddress(null);
        em.merge(emp);
        tx.commit();

        TypedQuery<Long> countQuery = em.createQuery(
                "select count(e) from Employee e where e.address is null and e.firstName = 'Alex'", Long.class);
        long numAdresses = countQuery.getSingleResult();
        assertEquals(1, numAdresses);

        TypedQuery<Long> q = em.createQuery("select count(a) from Address a", Long.class);
        long num = q.getSingleResult();
        assertEquals(0, num);
        em.close();
    }

    /**
     * Test updating an employee salary.
     */
    @Test
    public void test22_update_salary() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // all 'expensive' Employees, should only be one
        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.salary > 200000",
                EmployeePojo.class);
        EmployeePojo emp = q1.getSingleResult();
        emp.setSalary(300001d);
        em.merge(emp);
        tx.commit();

        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.salary > 300000",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();
        assertEquals(1, result.size());
        em.close();
    }

    /**
     * Test pojo listeners are creating timestamps.
     */
    @Test
    public void test23_listener_create_timestamp() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Long> countQuery = em.createQuery("select count(e) from Employee e where e.createdDate is null",
                Long.class);
        long numEmployees = countQuery.getSingleResult();

        // no employees should have an empty created date field
        assertEquals(0, numEmployees);
        em.close();
    }

    /**
     * Test updating an employee updates timestamp.
     */
    @Test
    public void test24_listener_update_timestamp() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.salary > 300000",
                EmployeePojo.class);
        EmployeePojo emp = q2.getSingleResult();
        LocalDateTime beforeUpdate = emp.getUpdatedDate();

        emp.setTitle("Programmer");
        em.merge(emp);
        tx.commit();

        LocalDateTime afterUpdate = emp.getUpdatedDate();

        assertNotEquals(beforeUpdate.getNano(), afterUpdate.getNano());
        em.close();
    }

    /**
     * Test employee versioning.
     */
    @Test
    public void test25_employee_version() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.salary > 300000",
                EmployeePojo.class);
        EmployeePojo emp = q2.getSingleResult();
        int beforeUpdate = emp.getVersion();

        emp.setLastName("Gagnier");
        em.merge(emp);
        tx.commit();

        int afterUpdate = emp.getVersion();

        assertNotEquals(beforeUpdate, afterUpdate);
        em.close();
    }

    /**
     * Test finishing a task.
     */
    @Test
    public void test26_task_finished() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo result = q1.getSingleResult();

        // assign tasks and finish first one
        List<EmployeeTask> tasks = new ArrayList<>();
        EmployeeTask task1 = new EmployeeTask();
        task1.setDescription("task 1");
        task1.setTaskDone(true);

        EmployeeTask task2 = new EmployeeTask();
        task2.setDescription("task 2");

        tasks.add(task1);
        tasks.add(task2);
        result.setTasks(tasks);

        em.merge(result);
        tx.commit();

        assertEquals(true, result.getTasks().get(0).isTaskDone());
        assertEquals(false, result.getTasks().get(1).isTaskDone());
        em.close();
    }

    /**
     * Test dates on tasks.
     */
    @Test
    public void test27_task_dates() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e where e.email = 'adam@gmail.com'",
                EmployeePojo.class);
        EmployeePojo result = q1.getSingleResult();

        result.getTasks().get(0).setTaskStart(LocalDateTime.now());
        result.getTasks().get(1).setTaskEndDate(LocalDateTime.now());

        em.merge(result);
        tx.commit();

        assertNotEquals(null, result.getTasks().get(0).getTaskStart());
        assertNotEquals(null, result.getTasks().get(1).getTaskEndDate());
        em.close();
    }

    /**
     * Test employees with salaries using criteria query.
     */
    @Test
    public void test28_employees_salaries() {
        EntityManager em = emf.createEntityManager();

        // Employees who are getting paid
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);

        query.where((queryBuilder.isNotNull(root.get(EmployeePojo_.salary))));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();

        // 2 employees with salaries thus far
        assertEquals(2, result2.size());
        em.close();
    }

    /**
     * Test employees with no address using criteria query.
     */
    @Test
    public void test29_employees_no_address() {
        EntityManager em = emf.createEntityManager();

        // Employees who have no address
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);

        query.where((queryBuilder.isNull(root.get(EmployeePojo_.address))));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();

        // 4 employees with no address thus far
        assertEquals(4, result2.size());
        em.close();
    }

    /**
     * Test employees with a title using criteria query.
     */
    @Test
    public void test30_employees_with_title() {
        EntityManager em = emf.createEntityManager();

        // Employees who have a title
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);

        query.where((queryBuilder.isNotNull(root.get(EmployeePojo_.title))));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();

        // 1 employee with a title thus far
        assertEquals(1, result2.size());
        em.close();
    }

    /**
     * Test projects with a name using criteria query.
     */
    @Test
    public void test31_projects_with_name() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        ProjectPojo proj1 = new ProjectPojo();
        proj1.setName("proj 2");

        em.persist(proj1);
        tx.commit();

        // Projects with name
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProjectPojo> query = queryBuilder.createQuery(ProjectPojo.class);
        Root<ProjectPojo> root = query.from(ProjectPojo.class);

        query.where((queryBuilder.isNotNull(root.get(ProjectPojo_.name))));
        List<ProjectPojo> result2 = em.createQuery(query).getResultList();

        // 1 project that was created above
        assertEquals(1, result2.size());
        em.close();
    }

    /**
     * Test phones with no employee attached using criteria query.
     */
    @Test
    public void test32_phones_no_employee() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // Phones with no owners
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PhonePojo> query = queryBuilder.createQuery(PhonePojo.class);
        Root<PhonePojo> root = query.from(PhonePojo.class);

        query.where((queryBuilder.isNull(root.get(PhonePojo_.owningEmployee))));
        List<PhonePojo> result2 = em.createQuery(query).getResultList();

        // 1 phone without an owner
        assertEquals(1, result2.size());
        em.close();
    }

    /**
     * Test every employee has auto generated primary key ID using criteria query.
     */
    @Test
    public void test33_employee_auto_id() {
        EntityManager em = emf.createEntityManager();

        // Employees without an id
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);

        query.where((queryBuilder.isNull(root.get(EmployeePojo_.id))));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();

        // all employees should have an id, 0 should be null
        assertEquals(0, result2.size());
        em.close();
    }

    /**
     * Test remaing phone count and type.
     */
    @Test
    public void test34_remaining_phones_type() {
        EntityManager em = emf.createEntityManager();

        // find all remaining phones
        TypedQuery<PhonePojo> q2 = em.createQuery("select p from Phone p", PhonePojo.class);
        List<PhonePojo> result = q2.getResultList();

        // 1 phone should be remaining and it should be a home phone with nobody assigned
        assertEquals(1, result.size());
        assertEquals(HomePhone.class, result.get(0).getClass());
        assertEquals(null, result.get(0).getOwningEmployee());
        em.close();
    }

    /**
     * Test employees with proper email.
     */
    @Test
    public void test35_employees_email() {
        EntityManager em = emf.createEntityManager();

        // Employees with a proper email
        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);

        query.where(queryBuilder.like(queryBuilder.upper(root.get(EmployeePojo_.email)), "%@%"));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();

        // 2 employees created with proper emails so far
        assertEquals(2, result2.size());
        em.close();
    }

    /**
     * Test employees created in April 2020.
     */
    @Test
    public void test36_employees_created_April() {
        EntityManager em = emf.createEntityManager();

        // find Employees that created in this month
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.createdDate like '2020-04-%'",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();

        // 4 employees created, as long as this test is ran in April it should pass
        assertEquals(4, result.size());
        em.close();
    }

    /**
     * Test finding employees missing a title.
     */
    @Test
    public void test37_employees_titles() {
        EntityManager em = emf.createEntityManager();

        // find Employees missing a title
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e where e.title is null",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();

        // only 1 employee has been assigned a title, 3 should have no title
        assertEquals(3, result.size());
        em.close();
    }

    /**
     * Test employees with a first and last name.
     */
    @Test
    public void test38_employees_full_name() {
        EntityManager em = emf.createEntityManager();

        // find Employees with first and last names
        TypedQuery<EmployeePojo> q2 = em.createQuery(
                "select e from Employee e where e.firstName is not null and e.lastName is not null",
                EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();

        // 2 employees should have full names
        assertEquals(2, result.size());
        em.close();
    }
    
    /**
     * Test home phones with a direction.
     */
    @Test
    public void test39_employees_home_phone_directions() {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HomePhone> query = queryBuilder.createQuery(HomePhone.class);
        Root<HomePhone> root = query.from(HomePhone.class);

        // find home phones with directions
        query.where(queryBuilder.like(queryBuilder.upper(root.get(HomePhone_.directions)), "L%"));
        List<HomePhone> result2 = em.createQuery(query).getResultList();

        // should be 1
        assertEquals(1, result2.size());
        em.close();
    }
    
    /**
     * Test mobile phones with a provider.
     */
    @Test
    public void test40_employees_mobile_phone_provider() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        MobilePhone phone = new MobilePhone();
        phone.setProvider("Rogers");
        
        EmployeePojo emp1 = em.find(EmployeePojo.class, 1);
        emp1.addPhone(phone);
        
        em.persist(phone);
        em.persist(emp1);
        tx.commit();

        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<MobilePhone> query = queryBuilder.createQuery(MobilePhone.class);
        Root<MobilePhone> root = query.from(MobilePhone.class);

        // find mobile phones with provider
        query.where(queryBuilder.like(queryBuilder.upper(root.get(MobilePhone_.provider)), "R%"));
        List<MobilePhone> result2 = em.createQuery(query).getResultList();

        // should be 1
        assertEquals(1, result2.size());
        em.close();
    }
    
    /**
     * Test work phones with a department.
     */
    @Test
    public void test41_employees_work_phone_department() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        WorkPhone phone = new WorkPhone();
        phone.setDepartment("Security");
        
        EmployeePojo emp1 = em.find(EmployeePojo.class, 1);
        emp1.addPhone(phone);
        
        em.persist(phone);
        em.persist(emp1);
        tx.commit();

        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<WorkPhone> query = queryBuilder.createQuery(WorkPhone.class);
        Root<WorkPhone> root = query.from(WorkPhone.class);

        // find work phones with a department
        query.where(queryBuilder.like(queryBuilder.upper(root.get(WorkPhone_.department)), "S%"));
        List<WorkPhone> result2 = em.createQuery(query).getResultList();

        // should be 1
        assertEquals(1, result2.size());
        em.close();
    }
    
    /**
     * Test employees first name start with A
     */
    @Test
    public void test42_employees_first_name() {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder queryBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EmployeePojo> query = queryBuilder.createQuery(EmployeePojo.class);
        Root<EmployeePojo> root = query.from(EmployeePojo.class);

        query.where(queryBuilder.like(queryBuilder.upper(root.get(EmployeePojo_.firstName)), "A%"));
        List<EmployeePojo> result2 = em.createQuery(query).getResultList();

        // should be 2, Alex and Adam
        assertEquals(2, result2.size());
        em.close();
    }
    
    /**
     * Test deleting all projects.
     */
    @Test
    public void test43_delete_all_projects() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // find all projects
        TypedQuery<ProjectPojo> q1 = em.createQuery("select p from Project p", ProjectPojo.class);
        List<ProjectPojo> result = q1.getResultList();

        // delete all projects
        for (ProjectPojo projectPojo : result) {
            em.remove(projectPojo);
        }
        tx.commit();

        // find remaining projects
        TypedQuery<ProjectPojo> q2 = em.createQuery("select p from Project p", ProjectPojo.class);
        List<ProjectPojo> finalResult = q2.getResultList();

        // 0 projects should be left
        assertEquals(0, finalResult.size());
        em.close();
    }
    
    /**
     * Test deleting all tasks.
     */
    @Test
    public void test44_delete_all_tasks() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // find all employees
        TypedQuery<EmployeePojo> q1 = em.createQuery("select e from Employee e", EmployeePojo.class);
        List<EmployeePojo> result = q1.getResultList();

        // delete all tasks
        for (EmployeePojo emps : result) {
            emps.setTasks(null);
        }
        tx.commit();

        // find remaining tasks
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e", EmployeePojo.class);
        List<EmployeePojo> finalResult = q2.getResultList();

        // 0 tasks shiould be left
        for (EmployeePojo emps : finalResult) {
            assertEquals(0, emps.getTasks().size());
        }
        em.close();
    }
    
    /**
     * Test address search like %.
     */
    @Test
    public void test45_address_search_like() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        AddressPojo address1 = new AddressPojo();
        address1.setCity("Kingston");
        em.persist(address1);

        AddressPojo address2 = new AddressPojo();
        address2.setCity("Oshawa");
        em.persist(address2);
        
        tx.commit();

        // find all addresses with names starting like new addresses above, should be 2
        TypedQuery<AddressPojo> q2 = em
                .createQuery("select a from Address a where a.city like 'K%' or a.city like 'O%'", AddressPojo.class);
        List<AddressPojo> result = q2.getResultList();

        assertEquals(2, result.size());
        em.close();
    }
    
    /**
     * Test deleting all addresses.
     */
    @Test
    public void test46_delete_all_addresses() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // find all addresses
        TypedQuery<AddressPojo> q1 = em.createQuery("select a from Address a", AddressPojo.class);
        List<AddressPojo> result = q1.getResultList();

        // delete all addresses
        for (AddressPojo address : result) {
            em.remove(address);
        }
        tx.commit();

        // find remaining addresses
        TypedQuery<AddressPojo> q2 = em.createQuery("select a from Address a", AddressPojo.class);
        List<AddressPojo> finalResult = q2.getResultList();

        // 0 addresses should be left
        assertEquals(0, finalResult.size());
        em.close();
    }
    
    /**
     * Test employee auto generated ids are proper.
     */
    @Test
    public void test47_employee_auto_ids() {
        EntityManager em = emf.createEntityManager();

        // find all employees
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e", EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();

        // increment count by one each loop which should correspond with employee's auto
        // generated, auto incremented id primary key from database
        int sum = 0;
        for (EmployeePojo employeePojo : result) {
            sum++;
            assertTrue(employeePojo.getId() == sum);
        }
        em.close();
    }
    
    /**
     * Test projects search like %.
     */
    @Test
    public void test48_projects_search_like() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        ProjectPojo proj1 = new ProjectPojo();
        proj1.setDescription("Group project Enterprise");
        em.persist(proj1);

        ProjectPojo proj2 = new ProjectPojo();
        proj2.setDescription("Group project Database");
        em.persist(proj2);

        ProjectPojo proj3 = new ProjectPojo();
        proj3.setDescription("Group project Software Dev");
        em.persist(proj3);

        tx.commit();

        // find all projects starting with Group, should be 3 above
        TypedQuery<ProjectPojo> q2 = em.createQuery("select p from Project p where p.description like 'Group%'",
                ProjectPojo.class);
        List<ProjectPojo> result = q2.getResultList();

        assertEquals(3, result.size());
        em.close();
    }

    /**
     * Test deleting all employees.
     */
    @Test
    public void test49_delete_all_employees() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // find all employees
        TypedQuery<EmployeePojo> q2 = em.createQuery("select e from Employee e", EmployeePojo.class);
        List<EmployeePojo> result = q2.getResultList();

        // assign last phone to check for orphan removal in next test
        TypedQuery<PhonePojo> q3 = em.createQuery("select p from Phone p", PhonePojo.class);
        List<PhonePojo> phones = q3.getResultList();
        result.get(0).addPhone(phones.get(0));
        em.merge(result.get(0));
        
        // delete all employees
        for (EmployeePojo employeePojo : result) {
            em.remove(employeePojo);
        }
        tx.commit();

        // find remaining employees
        TypedQuery<EmployeePojo> q4 = em.createQuery("select e from Employee e", EmployeePojo.class);
        List<EmployeePojo> finalResult = q4.getResultList();

        // 0 employees should be left
        assertEquals(0, finalResult.size());
        em.close();
    }

    /**
     * Test orphan removal of phones works.
     */
    @Test
    public void test50_phone_orphan_removal() {
        EntityManager em = emf.createEntityManager();

        // find all phones
        TypedQuery<PhonePojo> q1 = em.createQuery("select p from Phone p", PhonePojo.class);
        List<PhonePojo> phones = q1.getResultList();

        // all employees that were assigned phones have been deleted, no phones should
        // remain here because of orphan removal
        assertEquals(0, phones.size());
        em.close();
    }
    
}