public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando o Sistema de Gerenciamento da Biblioteca...");
        
        try {
            // Instancia o menu que criamos anteriormente
           Menu menu = new Menu();
           menu.menu();
            
        } catch (Exception e) {
            System.err.println("Erro crítico ao iniciar o sistema: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Sistema encerrado.");
    }
}