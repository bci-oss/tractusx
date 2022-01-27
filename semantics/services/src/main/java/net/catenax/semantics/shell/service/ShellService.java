package net.catenax.semantics.shell.service;

import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.repository.ShellRepository;
import org.springframework.stereotype.Service;

@Service
public class ShellService {

    private final ShellRepository repository;

    public ShellService(ShellRepository repository) {
        this.repository = repository;
    }

    public Shell save(Shell shell) {
        return repository.save(shell);
    }
}
