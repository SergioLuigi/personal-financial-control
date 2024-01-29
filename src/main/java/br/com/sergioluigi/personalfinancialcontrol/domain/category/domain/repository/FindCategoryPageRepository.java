package br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindCategoryPageRepository {
    Page<CategoryModel> execute(PageRequest pageRequest);
}
