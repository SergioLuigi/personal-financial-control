package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;

public interface FindCategoryPage {
    Page<CategoryModel> execute(@PageableDefault PageRequest pageRequest);
}
