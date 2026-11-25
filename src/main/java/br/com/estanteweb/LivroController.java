package br.com.estanteweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LivroController {

    @Autowired
    private LivroRepository repositorio;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/livros/html";
    }

    @GetMapping("/livros/html")
    public String listarHtml(@RequestParam(required = false) String ordem, Model model) {
        model.addAttribute("livros", repositorio.listarOrdenadoPor(ordem));
        return "livros";
    }

    @GetMapping("/livros/{id}")
    public String exibir(@PathVariable Long id, Model model) {
        Livro livro = repositorio.buscarPorId(id);
        model.addAttribute("livro", livro);
        return "livro";
    }

    @GetMapping("/livros/novo")
    public String novo(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }
        return "livro-novo";
    }

    @PostMapping("/livros")
    public String criar(@RequestParam String titulo, @RequestParam String autor,
            @RequestParam int ano, @RequestParam double preco,
            HttpSession session, Model model) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        if (titulo.isBlank() || autor.isBlank()) {
            model.addAttribute("erro", "Título e autor são obrigatórios.");
            return "livro-novo";
        }

        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setAno(ano);
        livro.setPreco(preco);
        repositorio.salvar(livro);

        return "redirect:/livros/html";
    }

    @GetMapping("/livros/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Livro livro = repositorio.buscarPorId(id);

        if (livro == null) {
            return "redirect:/livros/html";
        }

        model.addAttribute("livro", livro);
        return "livro-editar";
    }

    @PostMapping("/livros/{id}")
    public String alterar(@PathVariable Long id, @RequestParam String titulo,
            @RequestParam String autor, @RequestParam int ano,
            @RequestParam double preco, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setAno(ano);
        livro.setPreco(preco);
        repositorio.atualizar(livro);

        return "redirect:/livros/" + id;
    }

    @GetMapping("/livros/{id}/excluir")
    public String confirmarExclusao(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        Livro livro = repositorio.buscarPorId(id);

        if (livro == null) {
            return "redirect:/livros/html";
        }

        model.addAttribute("livro", livro);
        return "livro-excluir";
    }

    @PostMapping("/livros/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        repositorio.excluir(id);
        return "redirect:/livros/html";
    }
}
