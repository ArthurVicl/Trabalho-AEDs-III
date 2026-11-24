import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("serial")
public class MenuGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;
    private AutorDAO autorDAO;
    private EmprestimoDAO emprestimoDAO;
    private AutoriaDAO autoriaDAO;

    public MenuGUI() {
        try {
            livroDAO = new LivroDAO();
            usuarioDAO = new UsuarioDAO();
            autorDAO = new AutorDAO();
            emprestimoDAO = new EmprestimoDAO();
            autoriaDAO = new AutoriaDAO();
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
        setSize(450, 600);
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

        JPanel panelBotoes = new JPanel(new GridLayout(6, 1, 10, 10));
        panelBotoes.setBackground(corFundo);
        panelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40)); 

        JButton btnLivros = new JButton("1 - Gerenciar Livros");
        JButton btnUsuarios = new JButton("2 - Gerenciar Usuários");
        JButton btnAutores = new JButton("3 - Gerenciar Autores");
        JButton btnEmprestimos = new JButton("4 - Gerenciar Empréstimos");
        JButton btnBackup = new JButton("5 - Backup e Restauração (LZW)");
        JButton btnSair = new JButton("0 - Sair");

        // Estilizando botões principais
        JButton[] botoes = {btnLivros, btnUsuarios, btnAutores, btnEmprestimos, btnBackup, btnSair};
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
        btnBackup.addActionListener(e -> menuBackup());
        btnSair.addActionListener(e -> System.exit(0));
    }

    // =====================================================
    // LIVROS
    // =====================================================
    private void menuLivros() {
        JDialog dialog = new JDialog(this, "Gerenciar Livros", true);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 520);
        dialog.setLocationRelativeTo(this);

        JLabel lbl = new JLabel("GERENCIAR LIVROS", SwingConstants.CENTER);
        lbl.setFont(new Font("Impact", Font.ITALIC, 20));
        lbl.setForeground(new Color(50, 205, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        dialog.add(lbl, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(9, 1, 6, 6));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 35, 20, 35));

        String[] opcoes = {
            "1. Adicionar Livro",
            "2. Buscar Livro",
            "3. Atualizar Livro",
            "4. Excluir Livro",
            "5. Listar por Autor",
            "6. Pesquisar por Título (KMP)",
            "7. Vincular Autor ao Livro",
            "8. Desvincular Autor do Livro",
            "0. Voltar"
        };

        for (int i = 0; i < opcoes.length; i++) {
            final int index = i;
            JButton btn = new JButton(opcoes[i]);
            btn.setBackground(new Color(220, 20, 60));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                dialog.dispose();
                executarAcaoLivro(index);
            });
            panel.add(btn);
        }

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void executarAcaoLivro(int escolha) {
        if (escolha == 8) return; // Voltar

        try {
            switch (escolha) {
                case 0: // Adicionar
                    String titulo = JOptionPane.showInputDialog("Título do Livro:");
                    if (titulo == null || titulo.trim().isEmpty()) return;
                    
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
                    
                    Livro livro = new Livro(titulo, dataPub, categorias, avaliacao, preco);
                    int id = livroDAO.create(livro);
                    JOptionPane.showMessageDialog(this, "Livro inserido com sucesso! ID: " + id
                        + "\nUse \"Vincular Autor\" para associar autores.");
                    break;

                case 1: // Buscar
                    String idBusca = JOptionPane.showInputDialog("ID do Livro:");
                    if (idBusca != null) {
                        Livro l = livroDAO.read(Integer.parseInt(idBusca));
                        if (l != null) JOptionPane.showMessageDialog(this, l.toString() + "\nAutor(es): " + nomesAutores(l.getId()));
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
                        int idLivroDel = Integer.parseInt(idExcluir);
                        autoriaDAO.deleteAllFromLivro(idLivroDel);
                        if (livroDAO.delete(idLivroDel))
                            JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!");
                        else
                            JOptionPane.showMessageDialog(this, "Livro não encontrado.");
                    }
                    break;

                case 4: // Listar por Autor
                    String idAut = JOptionPane.showInputDialog("ID do Autor:");
                    if (idAut != null) {
                        List<Livro> livros = autoriaDAO.readLivrosDoAutor(Integer.parseInt(idAut), livroDAO);
                        if (livros == null || livros.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Nenhum livro encontrado para o Autor ID " + idAut);
                        } else {
                            StringBuilder sb = new StringBuilder("Livros do Autor:\n\n");
                            for (Livro b : livros)
                                sb.append(b.toString()).append("\nAutor(es): ").append(nomesAutores(b.getId())).append("\n\n");
                            JOptionPane.showMessageDialog(this, sb.toString());
                        }
                    }
                    break;

                case 5: // Pesquisar por Título (KMP)
                    String padrao = JOptionPane.showInputDialog(this, "Digite o padrão textual a pesquisar no título (Algoritmo KMP):");
                    if (padrao != null && !padrao.trim().isEmpty()) {
                        List<Livro> encontrados = livroDAO.buscarPorTituloKMP(padrao.trim());
                        if (encontrados.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Nenhum livro encontrado com o padrão: \"" + padrao + "\"");
                        } else {
                            StringBuilder sb = new StringBuilder("=== Livros Encontrados (KMP: \"" + padrao + "\") ===\n\n");
                            for (Livro l : encontrados) {
                                int pos = KMP.buscar(l.getTitulo(), padrao.trim());
                                sb.append("ID: ").append(l.getId()).append(" | ").append(l.getTitulo()).append("\n");
                                sb.append("Posição no título: caractere ").append(pos).append("\n");
                                sb.append("Autor(es): ").append(nomesAutores(l.getId())).append("\n");
                                sb.append("Categorias: ").append(l.getCategorias()).append("\n");
                                sb.append("Avaliação: ").append(l.getAvaliacao()).append(" | Preço: R$ ").append(String.format("%.2f", l.getPreco())).append("\n");
                                sb.append("--------------------------------------------------\n");
                            }
                            sb.append("Total de registros localizados: ").append(encontrados.size());

                            JTextArea ta = new JTextArea(sb.toString(), 15, 38);
                            ta.setEditable(false);
                            ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
                            JScrollPane sp = new JScrollPane(ta);
                            JOptionPane.showMessageDialog(this, sp, "Resultado da Pesquisa KMP", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    break;

                case 6: // Vincular Autor ao Livro
                case 7: // Desvincular Autor do Livro
                    boolean vincular = (escolha == 6);
                    String idLivroVStr = JOptionPane.showInputDialog("ID do Livro:");
                    if (idLivroVStr == null || idLivroVStr.trim().isEmpty()) return;
                    int idLivroV = Integer.parseInt(idLivroVStr.trim());
                    if (livroDAO.read(idLivroV) == null) {
                        JOptionPane.showMessageDialog(this, "Livro não encontrado.");
                        return;
                    }

                    String idAutorVStr = JOptionPane.showInputDialog("ID do Autor:");
                    if (idAutorVStr == null || idAutorVStr.trim().isEmpty()) return;
                    int idAutorV = Integer.parseInt(idAutorVStr.trim());
                    if (autorDAO.read(idAutorV) == null) {
                        JOptionPane.showMessageDialog(this, "Autor não encontrado.");
                        return;
                    }

                    if (vincular) {
                        if (autoriaDAO.create(idAutorV, idLivroV))
                            JOptionPane.showMessageDialog(this, "Autor vinculado ao livro!");
                        else
                            JOptionPane.showMessageDialog(this, "Esse autor já está vinculado ao livro.");
                    } else {
                        if (autoriaDAO.delete(idAutorV, idLivroV))
                            JOptionPane.showMessageDialog(this, "Autor desvinculado do livro!");
                        else
                            JOptionPane.showMessageDialog(this, "Vínculo não encontrado.");
                    }
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nomesAutores(int idLivro) throws Exception {
        List<Autor> autores = autoriaDAO.readAutoresDoLivro(idLivro, autorDAO);
        if (autores.isEmpty()) return "Nenhum autor vinculado";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < autores.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(autores.get(i).getNome());
        }
        return sb.toString();
    }

    // =====================================================
    // USUÁRIOS
    // =====================================================
    private void menuUsuarios() {
        JDialog dialog = new JDialog(this, "Gerenciar Usuários", true);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);

        JLabel lbl = new JLabel("GERENCIAR USUÁRIOS", SwingConstants.CENTER);
        lbl.setFont(new Font("Impact", Font.ITALIC, 20));
        lbl.setForeground(new Color(50, 205, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        dialog.add(lbl, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(6, 1, 6, 6));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 35, 20, 35));

        String[] opcoes = {
            "1. Adicionar Usuário",
            "2. Buscar Usuário",
            "3. Atualizar Usuário",
            "4. Excluir Usuário",
            "5. Ordenação Externa",
            "0. Voltar"
        };

        for (int i = 0; i < opcoes.length; i++) {
            final int index = i;
            JButton btn = new JButton(opcoes[i]);
            btn.setBackground(new Color(220, 20, 60));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                dialog.dispose();
                executarAcaoUsuario(index);
            });
            panel.add(btn);
        }

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void executarAcaoUsuario(int escolha) {
        if (escolha == 5) return; // Voltar

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
        JDialog dialog = new JDialog(this, "Gerenciar Autores", true);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JLabel lbl = new JLabel("GERENCIAR AUTORES", SwingConstants.CENTER);
        lbl.setFont(new Font("Impact", Font.ITALIC, 20));
        lbl.setForeground(new Color(50, 205, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        dialog.add(lbl, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 35, 20, 35));

        String[] opcoes = {
            "1. Adicionar Autor",
            "2. Buscar Autor",
            "3. Atualizar Autor",
            "4. Excluir Autor",
            "0. Voltar"
        };

        for (int i = 0; i < opcoes.length; i++) {
            final int index = i;
            JButton btn = new JButton(opcoes[i]);
            btn.setBackground(new Color(220, 20, 60));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                dialog.dispose();
                executarAcaoAutor(index);
            });
            panel.add(btn);
        }

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void executarAcaoAutor(int escolha) {
        if (escolha == 4) return; // Voltar

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
                        int idAutorDel = Integer.parseInt(idExcluir);
                        autoriaDAO.deleteAllFromAutor(idAutorDel);
                        if (autorDAO.delete(idAutorDel))
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
        JDialog dialog = new JDialog(this, "Gerenciar Empréstimos", true);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JLabel lbl = new JLabel("GERENCIAR EMPRÉSTIMOS", SwingConstants.CENTER);
        lbl.setFont(new Font("Impact", Font.ITALIC, 20));
        lbl.setForeground(new Color(50, 205, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        dialog.add(lbl, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 35, 20, 35));

        String[] opcoes = {
            "1. Adicionar Empréstimo",
            "2. Buscar Empréstimo",
            "3. Atualizar Empréstimo",
            "4. Excluir Empréstimo",
            "0. Voltar"
        };

        for (int i = 0; i < opcoes.length; i++) {
            final int index = i;
            JButton btn = new JButton(opcoes[i]);
            btn.setBackground(new Color(220, 20, 60));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                dialog.dispose();
                executarAcaoEmprestimo(index);
            });
            panel.add(btn);
        }

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void executarAcaoEmprestimo(int escolha) {
        if (escolha == 4) return; // Voltar

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

    // =====================================================
    // BACKUP E RESTAURAÇÃO (LZW) - FASE IV
    // =====================================================
    public static class ResultadoBackup {
        public long tamanhoOriginal;
        public long tamanhoCompactado;
        public double taxaCompressao;
        public int totalArquivos;
        public String caminhoArquivoBackup;

        public ResultadoBackup(long original, long compactado, double taxa, int total, String caminho) {
            this.tamanhoOriginal = original;
            this.tamanhoCompactado = compactado;
            this.taxaCompressao = taxa;
            this.totalArquivos = total;
            this.caminhoArquivoBackup = caminho;
        }

        @Override
        public String toString() {
            return String.format(
                "Backup gerado com sucesso via LZW!\n\n" +
                "Arquivo gerado: %s\n" +
                "Arquivos incluídos: %d\n" +
                "Tamanho original dos arquivos: %,d bytes\n" +
                "Tamanho do arquivo compactado: %,d bytes\n" +
                "Taxa de compressão: %.2f%%",
                caminhoArquivoBackup, totalArquivos, tamanhoOriginal, tamanhoCompactado, taxaCompressao
            );
        }
    }

    private void menuBackup() {
        JDialog dialog = new JDialog(this, "Backup e Restauração LZW", true);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);

        JLabel lbl = new JLabel("BACKUP E RESTAURAÇÃO (LZW)", SwingConstants.CENTER);
        lbl.setFont(new Font("Impact", Font.ITALIC, 20));
        lbl.setForeground(new Color(50, 205, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        dialog.add(lbl, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 1, 6, 6));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 35, 20, 35));

        String[] opcoes = {
            "1. Criar Backup Completo (LZW)",
            "2. Restaurar Backup Completo (LZW)",
            "0. Voltar"
        };

        for (int i = 0; i < opcoes.length; i++) {
            final int index = i;
            JButton btn = new JButton(opcoes[i]);
            btn.setBackground(new Color(220, 20, 60));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                dialog.dispose();
                executarAcaoBackup(index);
            });
            panel.add(btn);
        }

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void executarAcaoBackup(int escolha) {
        if (escolha == 2) return; // Voltar

        try {
            java.io.File pastaDados = new java.io.File("./dados");
            java.io.File arquivoBackup = new java.io.File("./backup_biblioteca.lzw");

            switch (escolha) {
                case 0: // Criar Backup
                    ResultadoBackup res = executarCriarBackup(pastaDados, arquivoBackup);
                    JOptionPane.showMessageDialog(this, res.toString(), "Backup Concluído", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case 1: // Restaurar Backup
                    if (!arquivoBackup.exists()) {
                        JOptionPane.showMessageDialog(this, "Arquivo de backup não encontrado: " + arquivoBackup.getAbsolutePath(), "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int conf = JOptionPane.showConfirmDialog(this, 
                            "A restauração irá sobrescrever todos os arquivos da pasta dados com o backup.\nDeseja continuar?",
                            "Confirmar Restauração", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (conf != JOptionPane.YES_OPTION) return;

                    fecharDAOs();
                    int totalRestaurados = executarRestaurarBackup(arquivoBackup, pastaDados);
                    reinicializarDAOs();

                    JOptionPane.showMessageDialog(this, 
                            "Restauração concluída com sucesso via LZW!\n\n" +
                            "Total de arquivos restaurados: " + totalRestaurados + "\n" +
                            "O banco de dados e os índices foram recarregados e estão operacionais.", 
                            "Restauração Concluída", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro no processo de backup/restauração: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            try {
                reinicializarDAOs();
            } catch (Exception ignored) {}
        }
    }

    private ResultadoBackup executarCriarBackup(java.io.File pastaDados, java.io.File arquivoDestino) throws Exception {
        if (!pastaDados.exists() || !pastaDados.isDirectory()) {
            throw new java.io.IOException("Diretório de dados não encontrado: " + pastaDados.getAbsolutePath());
        }

        java.util.List<java.io.File> arquivos = new java.util.ArrayList<>();
        listarArquivosRecursivo(pastaDados, arquivos);

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);

        long tamanhoOriginalTotal = 0;
        String caminhoBase = pastaDados.getCanonicalPath();

        for (java.io.File f : arquivos) {
            String caminhoAbs = f.getCanonicalPath();
            String relativo = caminhoAbs.substring(caminhoBase.length());
            if (relativo.startsWith(java.io.File.separator)) {
                relativo = relativo.substring(1);
            }
            relativo = relativo.replace('\\', '/');

            byte[] conteudo = lerBytesArquivo(f);
            tamanhoOriginalTotal += conteudo.length;

            dos.writeUTF(relativo);
            dos.writeInt(conteudo.length);
            dos.write(conteudo);
        }

        dos.writeUTF("");
        dos.flush();

        byte[] dadosEmpacotados = baos.toByteArray();
        byte[] dadosCompactados = LZW.comprimir(dadosEmpacotados);

        if (arquivoDestino.getParentFile() != null) {
            arquivoDestino.getParentFile().mkdirs();
        }
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(arquivoDestino)) {
            fos.write(dadosCompactados);
        }

        long tamanhoCompactado = arquivoDestino.length();
        double taxa = 0.0;
        if (tamanhoOriginalTotal > 0) {
            taxa = (1.0 - ((double) tamanhoCompactado / (double) tamanhoOriginalTotal)) * 100.0;
        }

        return new ResultadoBackup(tamanhoOriginalTotal, tamanhoCompactado, taxa, arquivos.size(), arquivoDestino.getName());
    }

    private int executarRestaurarBackup(java.io.File arquivoBackup, java.io.File pastaDestinoDados) throws Exception {
        if (!arquivoBackup.exists() || !arquivoBackup.isFile()) {
            throw new java.io.IOException("Arquivo de backup não encontrado: " + arquivoBackup.getAbsolutePath());
        }

        byte[] bytesCompactados = lerBytesArquivo(arquivoBackup);
        byte[] dadosDescompactados = LZW.descomprimir(bytesCompactados);

        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(dadosDescompactados);
        java.io.DataInputStream dis = new java.io.DataInputStream(bais);

        int restaurados = 0;
        while (true) {
            String relativo = dis.readUTF();
            if (relativo == null || relativo.isEmpty()) {
                break;
            }

            int tamanho = dis.readInt();
            byte[] conteudo = new byte[tamanho];
            dis.readFully(conteudo);

            java.io.File destFile = new java.io.File(pastaDestinoDados, relativo);
            if (destFile.getParentFile() != null) {
                destFile.getParentFile().mkdirs();
            }

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile)) {
                fos.write(conteudo);
            }
            restaurados++;
        }

        return restaurados;
    }

    private static void listarArquivosRecursivo(java.io.File dir, java.util.List<java.io.File> resultado) {
        java.io.File[] lista = dir.listFiles();
        if (lista != null) {
            for (java.io.File f : lista) {
                if (f.isDirectory()) {
                    listarArquivosRecursivo(f, resultado);
                } else if (f.isFile()) {
                    resultado.add(f);
                }
            }
        }
    }

    private static byte[] lerBytesArquivo(java.io.File f) throws java.io.IOException {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) {
            byte[] b = new byte[(int) f.length()];
            int lidos = 0;
            while (lidos < b.length) {
                int r = fis.read(b, lidos, b.length - lidos);
                if (r == -1) break;
                lidos += r;
            }
            return b;
        }
    }

    private void fecharDAOs() {
        try { if (livroDAO != null) livroDAO.close(); } catch (Exception ignored) {}
        try { if (usuarioDAO != null) usuarioDAO.close(); } catch (Exception ignored) {}
        try { if (autorDAO != null) autorDAO.close(); } catch (Exception ignored) {}
        try { if (emprestimoDAO != null) emprestimoDAO.close(); } catch (Exception ignored) {}
        try { if (autoriaDAO != null) autoriaDAO.close(); } catch (Exception ignored) {}
    }

    private void reinicializarDAOs() throws Exception {
        fecharDAOs();
        livroDAO = new LivroDAO();
        usuarioDAO = new UsuarioDAO();
        autorDAO = new AutorDAO();
        emprestimoDAO = new EmprestimoDAO();
        autoriaDAO = new AutoriaDAO();
    }

    // =====================================================
    // ALGORITMO KMP (Knuth-Morris-Pratt)
    // =====================================================
    public static class KMP {
        public static int[] computarLPS(String padrao) {
            int m = padrao.length();
            int[] lps = new int[m];
            int len = 0;
            int i = 1;
            lps[0] = 0;

            while (i < m) {
                if (padrao.charAt(i) == padrao.charAt(len)) {
                    len++;
                    lps[i] = len;
                    i++;
                } else {
                    if (len != 0) {
                        len = lps[len - 1];
                    } else {
                        lps[i] = 0;
                        i++;
                    }
                }
            }
            return lps;
        }

        public static int buscar(String texto, String padrao) {
            if (padrao == null || padrao.isEmpty()) return 0;
            if (texto == null || texto.isEmpty()) return -1;

            String t = toLower(texto);
            String p = toLower(padrao);

            int n = t.length();
            int m = p.length();
            if (m > n) return -1;

            int[] lps = computarLPS(p);

            int i = 0;
            int j = 0;

            while (i < n) {
                if (p.charAt(j) == t.charAt(i)) {
                    i++;
                    j++;
                }

                if (j == m) {
                    return i - j;
                } else if (i < n && p.charAt(j) != t.charAt(i)) {
                    if (j != 0) {
                        j = lps[j - 1];
                    } else {
                        i++;
                    }
                }
            }

            return -1;
        }

        public static boolean contem(String texto, String padrao) {
            return buscar(texto, padrao) != -1;
        }

        private static String toLower(String s) {
            if (s == null) return null;
            char[] chars = new char[s.length()];
            for (int i = 0; i < s.length(); i++) {
                chars[i] = Character.toLowerCase(s.charAt(i));
            }
            return new String(chars);
        }
    }

    // =====================================================
    // ALGORITMO LZW (Lempel-Ziv-Welch)
    // =====================================================
    public static class LZW {
        private static final int MAX_CODIGOS = 4096;
        private static final int CODIGOS_INICIAIS = 256;

        public static byte[] comprimir(byte[] entrada) throws IOException {
            if (entrada == null || entrada.length == 0) return new byte[0];

            Map<Long, Integer> dicionario = new HashMap<>();
            int proximoCodigo = CODIGOS_INICIAIS;

            List<Integer> codigos = new ArrayList<>();
            int p = -1;

            for (int i = 0; i < entrada.length; i++) {
                int b = entrada[i] & 0xFF;
                if (p == -1) {
                    p = b;
                } else {
                    long chave = ((long) p << 8) | b;
                    if (dicionario.containsKey(chave)) {
                        p = dicionario.get(chave);
                    } else {
                        codigos.add(p);
                        if (proximoCodigo < MAX_CODIGOS) {
                            dicionario.put(chave, proximoCodigo++);
                        }
                        p = b;
                    }
                }
            }

            if (p != -1) codigos.add(p);

            return empacotar12Bits(codigos);
        }

        public static byte[] descomprimir(byte[] compactado) throws IOException {
            if (compactado == null || compactado.length == 0) return new byte[0];

            List<Integer> codigos = desempacotar12Bits(compactado);
            if (codigos.isEmpty()) return new byte[0];

            byte[][] dicionario = new byte[MAX_CODIGOS][];
            for (int i = 0; i < CODIGOS_INICIAIS; i++) {
                dicionario[i] = new byte[] { (byte) i };
            }
            int proximoCodigo = CODIGOS_INICIAIS;

            ByteArrayOutputStream saida = new ByteArrayOutputStream();

            int codigoAntigo = codigos.get(0);
            saida.write(dicionario[codigoAntigo]);

            for (int i = 1; i < codigos.size(); i++) {
                int codigoAtual = codigos.get(i);
                byte[] entrada;

                if (codigoAtual < proximoCodigo) {
                    entrada = dicionario[codigoAtual];
                } else if (codigoAtual == proximoCodigo) {
                    byte[] cadeiaAntiga = dicionario[codigoAntigo];
                    entrada = new byte[cadeiaAntiga.length + 1];
                    System.arraycopy(cadeiaAntiga, 0, entrada, 0, cadeiaAntiga.length);
                    entrada[cadeiaAntiga.length] = cadeiaAntiga[0];
                } else {
                    throw new IOException("Código LZW inválido: " + codigoAtual);
                }

                saida.write(entrada);

                if (proximoCodigo < MAX_CODIGOS) {
                    byte[] cadeiaAntiga = dicionario[codigoAntigo];
                    byte[] novaEntrada = new byte[cadeiaAntiga.length + 1];
                    System.arraycopy(cadeiaAntiga, 0, novaEntrada, 0, cadeiaAntiga.length);
                    novaEntrada[cadeiaAntiga.length] = entrada[0];
                    dicionario[proximoCodigo++] = novaEntrada;
                }

                codigoAntigo = codigoAtual;
            }

            return saida.toByteArray();
        }

        private static byte[] empacotar12Bits(List<Integer> codigos) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(codigos.size());

            int buffer = 0;
            int bitsNoBuffer = 0;

            for (int codigo : codigos) {
                buffer = (buffer << 12) | (codigo & 0xFFF);
                bitsNoBuffer += 12;

                while (bitsNoBuffer >= 8) {
                    bitsNoBuffer -= 8;
                    baos.write((buffer >> bitsNoBuffer) & 0xFF);
                }
            }

            if (bitsNoBuffer > 0) {
                baos.write((buffer << (8 - bitsNoBuffer)) & 0xFF);
            }

            return baos.toByteArray();
        }

        private static List<Integer> desempacotar12Bits(byte[] compactado) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(compactado);
            DataInputStream dis = new DataInputStream(bais);

            int totalCodigos = dis.readInt();
            List<Integer> codigos = new ArrayList<>(totalCodigos);

            int buffer = 0;
            int bitsNoBuffer = 0;

            for (int i = 0; i < totalCodigos; i++) {
                while (bitsNoBuffer < 12) {
                    int b = bais.read();
                    if (b == -1) throw new IOException("Fim inesperado dos dados LZW.");
                    buffer = (buffer << 8) | (b & 0xFF);
                    bitsNoBuffer += 8;
                }

                bitsNoBuffer -= 12;
                int codigo = (buffer >> bitsNoBuffer) & 0xFFF;
                codigos.add(codigo);
            }

            return codigos;
        }
    }
}