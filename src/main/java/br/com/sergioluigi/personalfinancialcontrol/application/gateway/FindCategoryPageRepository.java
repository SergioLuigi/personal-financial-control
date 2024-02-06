package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindCategoryPageRepository {
    Page<CategoryModel> execute(PageRequest pageRequest);
}
