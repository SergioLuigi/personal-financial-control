package br.com.sergioluigi.personalfinancialcontrol.infra.controller;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.*;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.CategoryRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CreateCategory createCategory;
    private final DeleteCategory deleteCategory;
    private final FindCategoryById findCategoryById;
    private final FindCategoryPage findCategoryPage;
    private final UpdateCategory updateCategory;

    @GetMapping
    @ResponseStatus(OK)
    public Page<CategoryResponse> findPage(@PageableDefault Pageable pageable) {
        return this.findCategoryPage
                .execute(PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize(), pageable.getSort()))
                .map(CategoryResponse::new);
    }

    @ResponseStatus(OK)
    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable UUID id) {
        return new CategoryResponse(this.findCategoryById.execute(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CategoryResponse create(@RequestBody CategoryRequest categoryRequest) {
        return new CategoryResponse(this.createCategory.execute(categoryRequest.toModel()));
    }

    @ResponseStatus(OK)
    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable UUID id,
            @RequestBody CategoryRequest categoryRequest) {
        return new CategoryResponse(this.updateCategory.execute(id, categoryRequest.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        this.deleteCategory.execute(id);
    }
}
