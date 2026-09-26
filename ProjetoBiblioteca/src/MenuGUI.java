import java.awt.*;
import java.util.List;
import javax.swing.*;

public class MenuGUI extends JFrame {

    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;
    private AutorDAO autorDAO;
    private EmprestimoDAO emprestimoDAO;

    public MenuGUI() {
        try {
            livroDAO = new LivroDAO();
            usuarioDAO = new UsuarioDAO();
            autorDAO = new AutorDAO();
            emprestimoDAO = new EmprestimoDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar o banco de dados: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Configuração de cores do tema da aplicação
        Color corDestaque = new Color(220, 20, 60); 
        Color corFundo = new Color(30, 30, 30);
        Color corTextoSecundario = new Color(50, 205, 50);

        UIManager.put("OptionPane.background", corFundo);
        UIManager.put("Panel.background", corFundo);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", corDestaque);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 12));
        UIManager.put("TextField.background", Color.DARK_GRAY);
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.caretForeground", Color.WHITE);

        setTitle("Sistema de Gerenciamento da Biblioteca");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(corFundo);
        setLayout(new BorderLayout(10, 10)); 

        JLabel lblTitulo = new JLabel("SISTEMA DE BIBLIOTECA", SwingConstants.CENTER);
        // Carregando a imagem original (logo da biblioteca)
        try {
            java.io.File f = new java.io.File("logo_biblioteca.png");
            if (!f.exists()) {
                f = new java.io.File("../logo_biblioteca.png");
            }
            if (f.exists()) {
                ImageIcon originalIcon = new ImageIcon(f.getAbsolutePath());
                Image img = originalIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
                lblTitulo.setIcon(new ImageIcon(img));
            }
        } catch (Exception ex) {
            System.err.println("Erro ao carregar a imagem: " + ex.getMessage());
        }

        lblTitulo.setHorizontalTextPosition(JLabel.CENTER);
        lblTitulo.setVerticalTextPosition(JLabel.BOTTOM);
        lblTitulo.setFont(new Font("Impact", Font.ITALIC, 24));
        lblTitulo.setForeground(corTextoSecundario);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); 
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelBotoes = new JPanel(new GridLayout(5, 1, 10, 10));
        panelBotoes.setBackground(corFundo);
        panelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40)); 

        JButton btnLivros = new JButton("1 - Gerenciar Livros");
        JButton btnUsuarios = new JButton("2 - Gerenciar Usuários");
        JButton btnAutores = new JButton("3 - Gerenciar Autores");
        JButton btnEmprestimos = new JButton("4 - Gerenciar Empréstimos");
        JButton btnSair = new JButton("0 - Sair");

        // Estilizando botões principais
        JButton[] botoes = {btnLivros, btnUsuarios, btnAutores, btnEmprestimos, btnSair};
        for (JButton btn : botoes) {
            btn.setBackground(corDestaque);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setFocusPainted(false);
            panelBotoes.add(btn);
        }
        
        add(panelBotoes, BorderLayout.CENTER);

        btnLivros.addActionListener(e -> menuLivros());
        btnUsuarios.addActionListener(e -> menuUsuarios());
        btnAutores.addActionListener(e -> menuAutores());
        btnEmprestimos.addActionListener(e -> menuEmprestimos());
        btnSair.addActionListener(e -> System.exit(0));
    }

    // =====================================================
    // LIVROS
    // =====================================================
    private void menuLivros() {
        String[] opcoes = {"Adicionar Livro", "Buscar Livro", "Atualizar Livro", "Excluir Livro", "Listar por Autor", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(this, 
                "Escolha uma opção para Livros:", "Gerenciar Livros", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, opcoes, opcoes[0]);

        try {
            switch (escolha) {
                case 0: // Adicionar
                    String titulo = JOptionPane.showInputDialog("Título do Livro:");
                    if (titulo == null || titulo.trim().isEmpty()) return;
                    
                    String idAutorStr = JOptionPane.showInputDialog("ID do Autor:");
                    if (idAutorStr == null || idAutorStr.trim().isEmpty()) return;
                    int idAutor = Integer.parseInt(idAutorStr);
                    
                    Autor autor = autorDAO.read(idAutor);
                    if (autor == null) {
                        JOptionPane.showMessageDialog(this, "Autor não encontrado!");
                        return;
                    }
                    
                    String dataPub = JOptionPane.showInputDialog("Data de Publicação:");
                    if (dataPub == null) return;
                    
                    String categorias = JOptionPane.showInputDialog("Categorias:");
                    if (categorias == null) return;
                    
                    String avaliacaoStr = JOptionPane.showInputDialog("Avaliação:");
                    if (avaliacaoStr == null || avaliacaoStr.trim().isEmpty()) return;
                    float avaliacao = Float.parseFloat(avaliacaoStr);
                    
                    String precoStr = JOptionPane.showInputDialog("Preço:");
                    if (precoStr == null || precoStr.trim().isEmpty()) return;
                    float preco = Float.parseFloat(precoStr);
                    
                    Livro livro = new Livro(titulo, idAutor, dataPub, categorias, avaliacao, preco);
                    int id = livroDAO.create(livro);
                    JOptionPane.showMessageDialog(this, "Livro inserido com sucesso! ID: " + id);
                    break;

                case 1: // Buscar
                    String idBusca = JOptionPane.showInputDialog("ID do Livro:");
                    if (idBusca != null) {
                        Livro l = livroDAO.read(Integer.parseInt(idBusca));
                        if (l != null) JOptionPane.showMessageDialog(this, l.toString());
                        else JOptionPane.showMessageDialog(this, "Livro não encontrado.");
                    }
                    break;

                case 2: // Atualizar
                    String idAtualiza = JOptionPane.showInputDialog("ID do Livro a atualizar:");
                    if (idAtualiza != null) {
                        Livro l = livroDAO.read(Integer.parseInt(idAtualiza));
                        if (l != null) {
                            String novoTitulo = JOptionPane.showInputDialog("Novo Título:", l.getTitulo());
                            if (novoTitulo == null) return;
                            l.setTitulo(novoTitulo);

                            String novoIdAutorStr = JOptionPane.showInputDialog("Novo ID do Autor:", l.getIdAutor());
                            if (novoIdAutorStr == null) return;
                            int novoIdAutor = Integer.parseInt(novoIdAutorStr);
                            if (autorDAO.read(novoIdAutor) == null) {
                                JOptionPane.showMessageDialog(this, "Autor não encontrado!");
                                return;
                            }
                            l.setIdAutor(novoIdAutor);

                            l.setDataPublicacao(JOptionPane.showInputDialog("Nova Data de Publicação:", l.getDataPublicacao()));
                            l.setCategorias(JOptionPane.showInputDialog("Novas Categorias:", l.getCategorias()));
                            l.setAvaliacao(Float.parseFloat(JOptionPane.showInputDialog("Nova Avaliação:", l.getAvaliacao())));
                            l.setPreco(Float.parseFloat(JOptionPane.showInputDialog("Novo Preço:", l.getPreco())));

                            if (livroDAO.update(l)) JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Livro não encontrado.");
                        }
                    }
                    break;

                case 3: // Excluir
                    String idExcluir = JOptionPane.showInputDialog("ID do Livro para excluir:");
                    if (idExcluir != null) {
                        if (livroDAO.delete(Integer.parseInt(idExcluir)))
                            JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!");
                        else
                            JOptionPane.showMessageDialog(this, "Livro não encontrado.");
                    }
                    break;

                case 4: // Listar por Autor
                    String idAut = JOptionPane.showInputDialog("ID do Autor:");
                    if (idAut != null) {
                        List<Livro> livros = livroDAO.readByAutor(Integer.parseInt(idAut));
                        if (livros == null || livros.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Nenhum livro encontrado para o Autor ID " + idAut);
                        } else {
                            StringBuilder sb = new StringBuilder("Livros do Autor:\n\n");
                            for (Livro b : livros) sb.append(b.toString()).append("\n\n");
                            JOptionPane.showMessageDialog(this, sb.toString());
                        }
                    }
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // USUÁRIOS
    // =====================================================
    private void menuUsuarios() {
        String[] opcoes = {"Adicionar Usuário", "Buscar Usuário", "Atualizar Usuário", "Excluir Usuário", "Ordenar (Externa)", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(this, 
                "Escolha uma opção para Usuários:", "Gerenciar Usuários", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, opcoes, opcoes[0]);

        try {
            switch (escolha) {
                case 0: // Adicionar
                    String nome = JOptionPane.showInputDialog("Nome:");
                    if (nome == null) return;
                    String cpf = JOptionPane.showInputDialog("CPF:");
                    String email = JOptionPane.showInputDialog("E-mail:");
                    String tel = JOptionPane.showInputDialog("Telefone:");
                    
                    Usuario u = new Usuario(nome, cpf, email, tel);
                    int id = usuarioDAO.create(u);
                    JOptionPane.showMessageDialog(this, "Usuário inserido! ID: " + id);
                    break;

                case 1: // Buscar
                    String idBusca = JOptionPane.showInputDialog("ID do Usuário:");
                    if (idBusca != null) {
                        Usuario user = usuarioDAO.read(Integer.parseInt(idBusca));
                        if (user != null) JOptionPane.showMessageDialog(this, user.toString());
                        else JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
                    }
                    break;

                case 2: // Atualizar
                    String idAtualiza = JOptionPane.showInputDialog("ID do Usuário a atualizar:");
                    if (idAtualiza != null) {
                        Usuario user = usuarioDAO.read(Integer.parseInt(idAtualiza));
                        if (user != null) {
                            String novoNome = JOptionPane.showInputDialog("Novo Nome:", user.getNome());
                            if (novoNome == null) return;
                            user.setNome(novoNome);
                            user.setCpf(JOptionPane.showInputDialog("Novo CPF:", user.getCpf()));
                            user.setEmail(JOptionPane.showInputDialog("Novo E-mail:", user.getEmail()));
                            user.setTelefone(JOptionPane.showInputDialog("Novo Telefone:", user.getTelefone()));

                            if (usuarioDAO.update(user)) JOptionPane.showMessageDialog(this, "Usuário atualizado!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
                        }
                    }
                    break;

                case 3: // Excluir
                    String idExcluir = JOptionPane.showInputDialog("ID do Usuário para excluir:");
                    if (idExcluir != null) {
                        if (usuarioDAO.delete(Integer.parseInt(idExcluir)))
                            JOptionPane.showMessageDialog(this, "Usuário excluído!");
                        else
                            JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
                    }
                    break;

                case 4: // Ordenação Externa
                    OrdenacaoExternaUsuario ordenacao = new OrdenacaoExternaUsuario();
                    ordenacao.ordenarPorNome();
                    JOptionPane.showMessageDialog(this, "Processo de intercalação balanceada concluído!");
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // AUTORES
    // =====================================================
    private void menuAutores() {
        String[] opcoes = {"Adicionar Autor", "Buscar Autor", "Atualizar Autor", "Excluir Autor", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(this, 
                "Escolha uma opção para Autores:", "Gerenciar Autores", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, opcoes, opcoes[0]);

        try {
            switch (escolha) {
                case 0: // Adicionar
                    String nome = JOptionPane.showInputDialog("Nome:");
                    if (nome == null) return;
                    String dtNasc = JOptionPane.showInputDialog("Data de nascimento:");
                    String nac = JOptionPane.showInputDialog("Nacionalidade:");
                    
                    Autor a = new Autor(nome, dtNasc, nac);
                    int id = autorDAO.create(a);
                    JOptionPane.showMessageDialog(this, "Autor inserido! ID: " + id);
                    break;

                case 1: // Buscar
                    String idBusca = JOptionPane.showInputDialog("ID do Autor:");
                    if (idBusca != null) {
                        Autor autor = autorDAO.read(Integer.parseInt(idBusca));
                        if (autor != null) JOptionPane.showMessageDialog(this, autor.toString());
                        else JOptionPane.showMessageDialog(this, "Autor não encontrado.");
                    }
                    break;

                case 2: // Atualizar
                    String idAtualiza = JOptionPane.showInputDialog("ID do Autor a atualizar:");
                    if (idAtualiza != null) {
                        Autor autor = autorDAO.read(Integer.parseInt(idAtualiza));
                        if (autor != null) {
                            String novoNome = JOptionPane.showInputDialog("Novo Nome:", autor.getNome());
                            if (novoNome == null) return;
                            autor.setNome(novoNome);
                            autor.setDataNascimento(JOptionPane.showInputDialog("Nova Data:", autor.getDataNascimento()));
                            autor.setNacionalidade(JOptionPane.showInputDialog("Nova Nacionalidade:", autor.getNacionalidade()));

                            if (autorDAO.update(autor)) JOptionPane.showMessageDialog(this, "Autor atualizado!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Autor não encontrado.");
                        }
                    }
                    break;

                case 3: // Excluir
                    String idExcluir = JOptionPane.showInputDialog("ID do Autor para excluir:");
                    if (idExcluir != null) {
                        if (autorDAO.delete(Integer.parseInt(idExcluir)))
                            JOptionPane.showMessageDialog(this, "Autor excluído!");
                        else
                            JOptionPane.showMessageDialog(this, "Autor não encontrado.");
                    }
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // EMPRÉSTIMOS
    // =====================================================
    private void menuEmprestimos() {
        String[] opcoes = {"Adicionar Empréstimo", "Buscar Empréstimo", "Atualizar Empréstimo", "Excluir Empréstimo", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(this, 
                "Escolha uma opção para Empréstimos:", "Gerenciar Empréstimos", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, opcoes, opcoes[0]);

        try {
            switch (escolha) {
                case 0: // Adicionar
                    String idUserStr = JOptionPane.showInputDialog("ID do Usuário:");
                    if (idUserStr == null) return;
                    int idUser = Integer.parseInt(idUserStr);
                    if (usuarioDAO.read(idUser) == null) {
                        JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
                        return;
                    }

                    String idLivroStr = JOptionPane.showInputDialog("ID do Livro:");
                    if (idLivroStr == null) return;
                    int idLivro = Integer.parseInt(idLivroStr);
                    if (livroDAO.read(idLivro) == null) {
                        JOptionPane.showMessageDialog(this, "Livro não encontrado.");
                        return;
                    }

                    String dtEmp = JOptionPane.showInputDialog("Data do Empréstimo:");
                    String dtDev = JOptionPane.showInputDialog("Data da Devolução:");
                    float multa = Float.parseFloat(JOptionPane.showInputDialog("Valor da Multa:"));
                    
                    Emprestimo e = new Emprestimo(idUser, idLivro, dtEmp, dtDev, multa);
                    int id = emprestimoDAO.create(e);
                    JOptionPane.showMessageDialog(this, "Empréstimo realizado! ID: " + id);
                    break;

                case 1: // Buscar
                    String idBusca = JOptionPane.showInputDialog("ID do Empréstimo:");
                    if (idBusca != null) {
                        Emprestimo emp = emprestimoDAO.read(Integer.parseInt(idBusca));
                        if (emp != null) JOptionPane.showMessageDialog(this, emp.toString());
                        else JOptionPane.showMessageDialog(this, "Empréstimo não encontrado.");
                    }
                    break;

                case 2: // Atualizar
                    String idAtualiza = JOptionPane.showInputDialog("ID do Empréstimo a atualizar:");
                    if (idAtualiza != null) {
                        Emprestimo emp = emprestimoDAO.read(Integer.parseInt(idAtualiza));
                        if (emp != null) {
                            String novoUserStr = JOptionPane.showInputDialog("Novo ID do Usuário:", emp.getIdUsuario());
                            if (novoUserStr == null) return;
                            emp.setIdUsuario(Integer.parseInt(novoUserStr));

                            String novoLivroStr = JOptionPane.showInputDialog("Novo ID do Livro:", emp.getIdLivro());
                            if (novoLivroStr == null) return;
                            emp.setIdLivro(Integer.parseInt(novoLivroStr));

                            emp.setDataEmprestimo(JOptionPane.showInputDialog("Nova Data Empréstimo:", emp.getDataEmprestimo()));
                            emp.setDataDevolucao(JOptionPane.showInputDialog("Nova Data Devolução:", emp.getDataDevolucao()));
                            emp.setValorMulta(Float.parseFloat(JOptionPane.showInputDialog("Novo Valor Multa:", emp.getValorMulta())));

                            if (emprestimoDAO.update(emp)) JOptionPane.showMessageDialog(this, "Empréstimo atualizado!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Empréstimo não encontrado.");
                        }
                    }
                    break;

                case 3: // Excluir
                    String idExcluir = JOptionPane.showInputDialog("ID do Empréstimo para excluir:");
                    if (idExcluir != null) {
                        if (emprestimoDAO.delete(Integer.parseInt(idExcluir)))
                            JOptionPane.showMessageDialog(this, "Empréstimo excluído!");
                        else
                            JOptionPane.showMessageDialog(this, "Empréstimo não encontrado.");
                    }
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

