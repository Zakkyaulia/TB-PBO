import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;

public class PostLoginMenu {
    private static String username = "Admin"; // Ganti dengan username dinamis jika diperlukan
    private static HashSet<String> judulIdeSet = new HashSet<>();

    public static void program(Scanner scanner) {
        System.out.println("\nLogin berhasil! Selamat datang " + username + " di program pengajuan ide Tugas Besar PBO");

        // Hubungkan ke database setelah login berhasil
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Koneksi database berhasil!\n");
            } else {
                System.out.println("Gagal menghubungkan ke database. Program akan berhenti.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Terjadi kesalahan saat menghubungkan ke database: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n=== Silahkan pilih menu yang ingin anda lakukan ===");
            System.out.println("1. Masukkan Data");
            System.out.println("2. Tampilkan Rekap Data");
            System.out.println("3. Hapus Data");
            System.out.println("4. Update Judul");
            System.out.println("5. Cek Judul");
            System.out.println("6. Keluar");
            System.out.print("Pilih menu: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    insertData(scanner);
                    break;

                case "2":
                    retrieveData();
                    break;

                case "3":
                    deleteData(scanner);
                    break;

                case "4":
                    updateJudul(scanner);
                    break;

                case "5":
                    checkJudul(scanner);
                    break;

                case "6":
                    System.out.println("\nTerima kasih telah menggunakan sistem kami. Sampai jumpa!");
                    return;

                default:
                    System.out.println("\nPilihan tidak valid! Silakan coba lagi.\n");
            }
        }
    }

    private static void insertData(Scanner scanner) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.print("\nMasukkan Nama: ");
            String nama = scanner.nextLine();

            String nim;
            while (true) {
                System.out.print("Masukkan NIM: ");
                nim = scanner.nextLine();
                if (nim.matches("\\d+")) break;
                System.out.println("Input NIM tidak valid. Harap isi data dalam bentuk angka.");
            }

            String noHp;
            while (true) {
                System.out.print("Masukkan No. HP: ");
                noHp = scanner.nextLine();
                if (noHp.matches("\\d+")) break;
                System.out.println("Input No. HP tidak valid. Harap isi data dalam bentuk angka.");
            }

            String tema;
            while (true) {
                System.out.print("Masukkan Tema Ide : ");
                tema = scanner.nextLine().toLowerCase();
                if (tema.equals("pendidikan") || tema.equals("kesehatan") || tema.equals("hiburan") || tema.equals("sosial")) break;
                System.out.println("Tema tidak valid. Tema yang dapat dipilih yaitu Pendidikan, Kesehatan, Hiburan, dan Sosial.");
            }

            String judulIde;
            while (true) {
                System.out.print("Masukkan Judul Ide: ");
                judulIde = scanner.nextLine().toUpperCase();
                if (judulIdeSet.contains(judulIde)) {
                    System.out.println("\nJudul telah terdaftar. Harap masukkan judul lain.");
                } else {
                    judulIdeSet.add(judulIde);
                    break;
                }
            }

            String sql = "INSERT INTO ide_tb (nama, nim, no_hp, tema, judul_ide) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nama);
            preparedStatement.setString(2, nim);
            preparedStatement.setString(3, noHp);
            preparedStatement.setString(4, tema);
            preparedStatement.setString(5, judulIde);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("\nData berhasil dimasukkan ke database.");
            }
        } catch (SQLException e) {
            System.out.println("\nGagal memasukkan data: " + e.getMessage());
        }
    }

    private static void retrieveData() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT nama, nim, no_hp, tema, judul_ide FROM ide_tb";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n=== Rekap Semua Data ===");
            int i = 1;
            while (resultSet.next()) {
                System.out.println("\nData ke-" + i + ":");
                System.out.println("Nama: " + resultSet.getString("nama"));
                System.out.println("NIM: " + resultSet.getString("nim"));
                System.out.println("No. HP: " + resultSet.getString("no_hp"));
                System.out.println("Tema Ide: " + resultSet.getString("tema"));
                System.out.println("Judul Ide: " + resultSet.getString("judul_ide"));
                i++;
            }
        } catch (SQLException e) {
            System.out.println("\nGagal mengambil data: " + e.getMessage());
        }
    }

    private static void deleteData(Scanner scanner) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.print("\nMasukkan Judul Ide yang ingin dihapus: ");
            String judulIde = scanner.nextLine().toUpperCase();

            String sql = "DELETE FROM ide_tb WHERE judul_ide = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, judulIde);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("\nData berhasil dihapus dari database.");
            } else {
                System.out.println("\nData tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("\nGagal menghapus data: " + e.getMessage());
        }
    }

    private static void updateJudul(Scanner scanner) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.print("\nMasukkan Judul Lama yang ingin diupdate: ");
            String judulLama = scanner.nextLine().toUpperCase();

            String sqlCheck = "SELECT * FROM ide_tb WHERE judul_ide = ?";
            PreparedStatement checkStatement = connection.prepareStatement(sqlCheck);
            checkStatement.setString(1, judulLama);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                String judulBaru;
                while (true) {
                    System.out.print("Masukkan Judul Baru: ");
                    judulBaru = scanner.nextLine().toUpperCase();
                    if (!judulIdeSet.contains(judulBaru)) break;
                    System.out.println("\nJudul telah terdaftar. Harap masukkan judul lain.");
                }

                String sqlUpdate = "UPDATE ide_tb SET judul_ide = ? WHERE judul_ide = ?";
                PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate);
                updateStatement.setString(1, judulBaru);
                updateStatement.setString(2, judulLama);

                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    judulIdeSet.remove(judulLama);
                    judulIdeSet.add(judulBaru);
                    System.out.println("\nJudul berhasil diperbarui.");
                }
            } else {
                System.out.println("\nJudul lama tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("\nGagal memperbarui data: " + e.getMessage());
        }
    }

    private static void checkJudul(Scanner scanner) {
        System.out.print("\nMasukkan Judul yang ingin diperiksa: ");
        String judulToCheck = scanner.nextLine().toUpperCase();

        if (judulIdeSet.contains(judulToCheck)) {
            System.out.println("\nJudul \"" + judulToCheck + "\" sudah terdaftar.");
        } else {
            System.out.println("\nJudul \"" + judulToCheck + "\" belum terdaftar dan dapat digunakan.");
        }
    }
}
