package net.catenax.semantics.shell.dto;

import lombok.Builder;
import lombok.Value;
import net.catenax.semantics.shell.model.Shell;

import java.util.List;

@Value
@Builder
public class ShellCollectionDto {
    List<Shell> items;
    Integer totalItems;
    Integer currentPage;
    Integer totalPages;
    Integer itemCount;
}