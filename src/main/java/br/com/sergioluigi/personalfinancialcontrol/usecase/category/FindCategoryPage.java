package br.com.sergioluigi.personalfinancialcontrol.usecase.category;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;

public interface FindCategoryPage {
    Page<CategoryModel> execute(@PageableDefault PageRequest pageRequest);
}
