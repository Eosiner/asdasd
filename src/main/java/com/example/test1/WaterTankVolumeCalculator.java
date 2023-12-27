package com.example.test1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class TandonApp extends Application {

    private ObservableList<Tandon> tandonList = FXCollections.observableArrayList();
    private ListView<Tandon> listView = new ListView<>();

    public static class Tandon {
        private String namaToko;
        private int jumlahTandon;
        private double tinggiTandon;
        private double diameterTandon;

        public Tandon(String namaToko, int jumlahTandon, double tinggiTandon, double diameterTandon) {
            this.namaToko = namaToko;
            this.jumlahTandon = jumlahTandon;
            this.tinggiTandon = tinggiTandon;
            this.diameterTandon = diameterTandon;
        }

        public String getNamaToko() {
            return namaToko;
        }

        public int getJumlahTandon() {
            return jumlahTandon;
        }

        public double getVolume() {
            // Hitung volume tandon (V = π * r^2 * h)
            double radius = diameterTandon / 2.0;
            return Math.PI * Math.pow(radius, 2) * tinggiTandon;
        }

        public double getTotalHarga() {
            // Harga per liter air Rp. 500
            return getVolume() * 500;
        }

        @Override
        public String toString() {
            return "Toko: " + namaToko + ", Jumlah Tandon: " + jumlahTandon +
                    ", Volume: " + getVolume() + " liter, Total Harga: Rp." + getTotalHarga();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Tandon Air");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        listView.setItems(tandonList);

        Button addButton = new Button("Tambah Tandon");
        addButton.setOnAction(event -> showAddDialog());

        Button editButton = new Button("Edit Tandon");
        editButton.setOnAction(event -> showEditDialog());

        Button deleteButton = new Button("Hapus Tandon");
        deleteButton.setOnAction(event -> deleteTandon());

        grid.add(listView, 0, 0, 3, 1);
        grid.add(addButton, 0, 1);
        grid.add(editButton, 1, 1);
        grid.add(deleteButton, 2, 1);

        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAddDialog() {
        Dialog<Tandon> dialog = createTandonDialog("Tambah Tandon");
        Optional<Tandon> result = dialog.showAndWait();
        result.ifPresent(tandon -> tandonList.add(tandon));
    }

    private void showEditDialog() {
        Tandon selectedTandon = listView.getSelectionModel().getSelectedItem();
        if (selectedTandon != null) {
            Dialog<Tandon> dialog = createTandonDialog("Edit Tandon");
            fillDialogFields(dialog, selectedTandon);
            Optional<Tandon> result = dialog.showAndWait();
            result.ifPresent(tandon -> {
                int index = tandonList.indexOf(selectedTandon);
                tandonList.set(index, tandon);
            });
        } else {
            showAlert("Pilih Tandon yang ingin diubah");
        }
    }

    private void deleteTandon() {
        Tandon selectedTandon = listView.getSelectionModel().getSelectedItem();
        if (selectedTandon != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Konfirmasi");
            confirmationAlert.setHeaderText("Hapus Tandon");
            confirmationAlert.setContentText("Apakah Anda yakin ingin menghapus tandon ini?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                tandonList.remove(selectedTandon);
            }
        } else {
            showAlert("Pilih Tandon yang ingin dihapus");
        }
    }

    private Dialog<Tandon> createTandonDialog(String title) {
        Dialog<Tandon> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType addButtonType = new ButtonType("Tambah", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField namaTokoField = new TextField();
        TextField jumlahTandonField = new TextField();
        TextField tinggiTandonField = new TextField();
        TextField diameterTandonField = new TextField();

        gridPane.add(new Label("Nama Toko:"), 0, 0);
        gridPane.add(namaTokoField, 1, 0);
        gridPane.add(new Label("Jumlah Tandon:"), 0, 1);
        gridPane.add(jumlahTandonField, 1, 1);
        gridPane.add(new Label("Tinggi Tandon (meter):"), 0, 2);
        gridPane.add(tinggiTandonField, 1, 2);
        gridPane.add(new Label("Diameter Tandon (meter):"), 0, 3);
        gridPane.add(diameterTandonField, 1, 3);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String namaToko = namaTokoField.getText();
                    int jumlahTandon = Integer.parseInt(jumlahTandonField.getText());
                    double tinggiTandon = Double.parseDouble(tinggiTandonField.getText());
                    double diameterTandon = Double.parseDouble(diameterTandonField.getText());

                    return new Tandon(namaToko, jumlahTandon, tinggiTandon, diameterTandon);
                } catch (NumberFormatException e) {
                    showAlert("Masukkan angka yang valid untuk Jumlah Tandon, Tinggi Tandon, dan Diameter Tandon");
                }
            }
            return null;
        });

        return dialog;
    }

    private void fillDialogFields(Dialog<Tandon> dialog, Tandon tandon) {
        DialogPane dialogPane = dialog.getDialogPane();
        ((TextField) dialogPane.lookup(".dialog-pane .text-field")).setEditable(false);

        TextField namaTokoField = (TextField) dialogPane.lookup(".dialog-pane .text-field");
        TextField jumlahTandonField = (TextField) dialogPane.lookup(".dialog-pane .text-field");
        TextField tinggiTandonField = (TextField) dialogPane.lookup(".dialog-pane .text-field");
        TextField diameterTandonField = (TextField) dialogPane.lookup(".dialog-pane .text-field");

        namaTokoField.setText(tandon.getNamaToko());
        jumlahTandonField.setText(Integer.toString(tandon.getJumlahTandon()));
        tinggiTandonField.setText(Double.toString(tandon.getTinggiTandon()));
        diameterTandonField.setText(Double.toString(tandon.getDiameterTandon()));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
