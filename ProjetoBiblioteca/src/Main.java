public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando o Sistema de Gerenciamento da Biblioteca...");
        
        try {
            MenuGUI menuGui = new MenuGUI();
            menuGui.setVisible(true);
            
        } catch (Exception e) {
            System.err.println("Erro crítico ao iniciar o sistema: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Sistema encerrado.");
    }
}