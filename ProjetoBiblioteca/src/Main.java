public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando o Sistema de Gerenciamento da Biblioteca...");
        
        try {
            // Instancia o menu que criamos anteriormente
            MenuLivros menu = new MenuLivros();
            
            // Chama a função que exibe as opções no console
            menu.menu();
            
        } catch (Exception e) {
            System.err.println("Erro crítico ao iniciar o sistema: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Sistema encerrado.");
    }
}