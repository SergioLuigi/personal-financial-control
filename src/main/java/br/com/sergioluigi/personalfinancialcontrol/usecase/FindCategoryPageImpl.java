package br.com.sergioluigi.personalfinancialcontrol.usecase;


import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindCategoryPage;
import br.com.sergioluigi.personalfinancialcontrol.domain.CategoryModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCategoryPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCategoryPageImpl implements FindCategoryPage {

    private final FindCategoryPageRepository findCategoryPageRepository;

    @Override
    public Page<CategoryModel> execute(@PageableDefault PageRequest pageRequest) {
        return findCategoryPageRepository.execute(pageRequest);
    }
}
