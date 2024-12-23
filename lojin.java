import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

// Interface untuk Captcha
interface Captcha {
    int[] generateQuestion();
    int getCorrectAnswer(int[] numbers);
}

// Implementasi Captcha untuk penjumlahan
class AdditionCaptcha implements Captcha {
    @Override
    public int[] generateQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(50) + 1;
        int num2 = random.nextInt(50) + 1;
        return new int[]{num1, num2};
    }

    @Override
    public int getCorrectAnswer(int[] numbers) {
        return numbers[0] + numbers[1];
    }
}

// Implementasi Captcha untuk pengurangan
class SubtractionCaptcha implements Captcha {
    @Override
    public int[] generateQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(50) + 1;
        int num2 = random.nextInt(50) + 1;
        if (num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }
        return new int[]{num1, num2};
    }

    @Override
    public int getCorrectAnswer(int[] numbers) {
        return numbers[0] - numbers[1];
    }
}

// Class Login dengan inheritance
class Login {
    String username;
    String password;

    // Constructor
    Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Metode untuk mengecek login
    boolean checkLogin(String inputUsername, String inputPassword) {
        return inputUsername.equals(username) && inputPassword.equals(password);
    }
}

// Main class
public class lojin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Silahkan login terlebih dahulu =====");

        // Akun default
        String regUsername = "admin";
        String regPassword = "password";

        // Membuat objek Login dengan akun default
        Login akun = new Login(regUsername, regPassword);

        // Login
        boolean isLoggedIn = false;

        while (!isLoggedIn) {
            // Input username dan password
            System.out.print("Masukkan username: ");
            String inputUsername = scanner.nextLine();

            System.out.print("Masukkan password: ");
            String inputPassword = scanner.nextLine();

            // Validasi username dan password terlebih dahulu
            if (akun.checkLogin(inputUsername, inputPassword)) {
                System.out.println("\nLogin berhasil! Lanjutkan dengan menyelesaikan Captcha.");

                // Captcha pertama: penjumlahan
                Captcha additionCaptcha = new AdditionCaptcha();
                if (validateCaptcha(additionCaptcha, scanner)) {
                    // Captcha kedua: pengurangan
                    Captcha subtractionCaptcha = new SubtractionCaptcha();
                    if (validateCaptcha(subtractionCaptcha, scanner)) {
                        isLoggedIn = true;
                    }
                }
            } else {
                System.out.println("\nUsername atau password salah! Silakan coba lagi.\n");
            }
        }

        // Menampilkan tanggal dan waktu login berhasil
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        
        System.out.println("\nLogin sukses! Selamat datang di sistem.");
        System.out.println("Waktu login: " + formattedDateTime);

        PostLoginMenu.program(scanner);
        scanner.close();
    }

    // Metode untuk validasi Captcha
    public static boolean validateCaptcha(Captcha captcha, Scanner scanner) {
        int[] question = captcha.generateQuestion();
        int correctAnswer = captcha.getCorrectAnswer(question);

        // Tentukan simbol operasi berdasarkan tipe Captcha
        String operation;
        if (captcha instanceof AdditionCaptcha) {
            operation = "+"; // Penjumlahan
        } else if (captcha instanceof SubtractionCaptcha) {
            operation = "-"; // Pengurangan
        } else {
            throw new IllegalArgumentException("Jenis Captcha tidak dikenal");
        }

        while (true) {
            // Tampilkan soal dengan simbol operasi yang sesuai
            System.out.println("Berapa hasil dari: " + question[0] + " " + operation + " " + question[1] + "?");
            System.out.print("Masukkan jawaban: ");

            int userAnswer;
            try {
                userAnswer = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("\nInput tidak valid! Silakan coba lagi.\n");
                continue;
            }

            if (userAnswer == correctAnswer) {
                return true;
            } else {
                System.out.println("\nJawaban salah! Silakan coba lagi.\n");
            }
        }
    }
}
