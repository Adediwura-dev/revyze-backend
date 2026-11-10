package com.revyze.practice.service;

import com.revyze.practice.entity.QuizQuestion;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizGenerationService {

    public List<QuizQuestion> generateQuestions(UUID quizId, String topic, int count) {
        String normalized = topic.toLowerCase();
        List<QuestionTemplate> templates = new ArrayList<>();

        if (normalized.contains("recur")) {
            templates.add(new QuestionTemplate(
                    "What is the primary condition needed to prevent infinite recursion in a recursive function?",
                    "A global counter variable",
                    "A base case that halts recursion",
                    "A loop statement",
                    "A stack overflow handler",
                    "B",
                    "A base case provides the termination condition that stops further recursive calls."
            ));
            templates.add(new QuestionTemplate(
                    "What data structure is used by the call runtime to maintain the execution context of recursive calls?",
                    "Call Stack",
                    "Queue",
                    "Binary Heap",
                    "Hash Table",
                    "A",
                    "The program execution call stack stores activation records for each active function invocation."
            ));
            templates.add(new QuestionTemplate(
                    "What error occurs when a recursive function exceeds its call stack depth limit?",
                    "OutOfMemoryError",
                    "StackOverflowError",
                    "NullPointerException",
                    "DeadlockException",
                    "B",
                    "StackOverflowError is raised when call stack memory is exhausted by unbounded recursive invocations."
            ));
            templates.add(new QuestionTemplate(
                    "What is a tail-recursive function?",
                    "A function that returns multiple recursive calls",
                    "A function where the recursive call is the final operation in the function",
                    "A function that uses tail pointers",
                    "A function with no parameters",
                    "B",
                    "In tail recursion, the recursive call is the very last statement, allowing compiler optimization without extra stack frames."
            ));
            templates.add(new QuestionTemplate(
                    "What is the time complexity of a standard naive recursive Fibonacci algorithm?",
                    "O(N)",
                    "O(log N)",
                    "O(2^N)",
                    "O(N^2)",
                    "C",
                    "The naive recursive implementation branches into two recursive calls at each step, yielding O(2^N) exponential complexity."
            ));
        } else if (normalized.contains("data") || normalized.contains("normaliz") || normalized.contains("sql")) {
            templates.add(new QuestionTemplate(
                    "What is the primary goal of First Normal Form (1NF)?",
                    "Eliminate partial dependencies",
                    "Ensure all column values are atomic and each record is unique",
                    "Eliminate transitive dependencies",
                    "Create foreign key constraints",
                    "B",
                    "1NF mandates that table attributes contain only atomic (indivisible) values with no repeating groups."
            ));
            templates.add(new QuestionTemplate(
                    "Second Normal Form (2NF) resolves which type of database dependency?",
                    "Transitive dependency",
                    "Partial dependency on a composite primary key",
                    "Multi-valued dependency",
                    "Cyclic dependency",
                    "B",
                    "2NF requires 1NF and ensures every non-prime attribute is fully functionally dependent on the whole primary key."
            ));
            templates.add(new QuestionTemplate(
                    "Third Normal Form (3NF) eliminates which condition?",
                    "Partial dependencies",
                    "Transitive functional dependencies",
                    "Non-unique candidate keys",
                    "Indexed column scans",
                    "B",
                    "3NF eliminates transitive dependencies (where a non-key attribute depends on another non-key attribute)."
            ));
            templates.add(new QuestionTemplate(
                    "Which SQL clause is used to filter records resulting from an aggregate function like COUNT() or AVG()?",
                    "WHERE",
                    "HAVING",
                    "GROUP BY",
                    "ORDER BY",
                    "B",
                    "HAVING filters grouped aggregates, whereas WHERE filters individual rows before grouping."
            ));
            templates.add(new QuestionTemplate(
                    "What does ACID stand for in relational database transactions?",
                    "Access, Control, Integrity, Durability",
                    "Atomicity, Consistency, Isolation, Durability",
                    "Authentication, Concurrency, Isolation, Deletion",
                    "Automation, Cache, Index, Distribution",
                    "B",
                    "ACID ensures reliable database transaction processing: Atomicity, Consistency, Isolation, and Durability."
            ));
        } else {
            // General Computer Science & Engineering Questions
            templates.add(new QuestionTemplate(
                    "What is the average time complexity of searching for an element in a balanced Binary Search Tree (BST)?",
                    "O(1)",
                    "O(log N)",
                    "O(N)",
                    "O(N log N)",
                    "B",
                    "In a balanced BST, each comparison halves the remaining search space, giving logarithmic O(log N) time."
            ));
            templates.add(new QuestionTemplate(
                    "Which data structure operates on a First-In, First-Out (FIFO) discipline?",
                    "Stack",
                    "Queue",
                    "Priority Queue",
                    "Tree",
                    "B",
                    "Queues strictly preserve FIFO order: elements added first are dequeued first."
            ));
            templates.add(new QuestionTemplate(
                    "Which of the following sorting algorithms guarantees an O(N log N) worst-case time complexity?",
                    "QuickSort",
                    "MergeSort",
                    "BubbleSort",
                    "InsertionSort",
                    "B",
                    "MergeSort consistently divides and conquers in O(N log N) time even in the worst case."
            ));
            templates.add(new QuestionTemplate(
                    "What is the function of the Virtual Memory subsystem in an Operating System?",
                    "Increase CPU clock speed",
                    "Allow execution of processes that may not be completely in physical RAM",
                    "Provide persistent storage after power down",
                    "Prevent network intrusion",
                    "B",
                    "Virtual memory maps virtual addresses to physical pages and disk swap, decoupling logical memory from physical RAM."
            ));
            templates.add(new QuestionTemplate(
                    "In Object-Oriented Programming, what principle hides internal state and requires all interaction to be performed through an object's methods?",
                    "Inheritance",
                    "Encapsulation",
                    "Polymorphism",
                    "Abstraction",
                    "B",
                    "Encapsulation bundles data with methods that operate on that data and restricts direct access to components."
            ));
        }

        int targetCount = Math.max(1, Math.min(count, templates.size()));
        List<QuizQuestion> questions = new ArrayList<>();

        for (int i = 0; i < targetCount; i++) {
            QuestionTemplate qt = templates.get(i);
            questions.add(new QuizQuestion(
                    quizId,
                    i,
                    qt.text,
                    qt.a,
                    qt.b,
                    qt.c,
                    qt.d,
                    qt.correct,
                    qt.explanation
            ));
        }

        return questions;
    }

    private static class QuestionTemplate {
        final String text;
        final String a;
        final String b;
        final String c;
        final String d;
        final String correct;
        final String explanation;

        QuestionTemplate(String text, String a, String b, String c, String d, String correct, String explanation) {
            this.text = text;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.correct = correct;
            this.explanation = explanation;
        }
    }
}
