package br.com.sergioluigi.personalfinancialcontrol.domain.category.usecase;


import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.model.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.category.domain.repository.FindCategoryPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCategoryPage {

    private final FindCategoryPageRepository findCategoryPageRepository;

    public Page<CategoryModel> execute(@PageableDefault PageRequest pageRequest) {
        return findCategoryPageRepository.execute(pageRequest);
    }
}
