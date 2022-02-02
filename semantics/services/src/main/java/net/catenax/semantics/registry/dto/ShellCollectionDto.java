package net.catenax.semantics.registry.dto;

import lombok.Builder;
import lombok.Value;
import net.catenax.semantics.registry.model.Shell;

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