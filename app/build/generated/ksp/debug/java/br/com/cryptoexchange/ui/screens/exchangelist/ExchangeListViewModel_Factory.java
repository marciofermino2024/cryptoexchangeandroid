package br.com.cryptoexchange.ui.screens.exchangelist;

import br.com.cryptoexchange.domain.usecase.GetExchangeListUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ExchangeListViewModel_Factory implements Factory<ExchangeListViewModel> {
  private final Provider<GetExchangeListUseCase> getExchangeListUseCaseProvider;

  public ExchangeListViewModel_Factory(
      Provider<GetExchangeListUseCase> getExchangeListUseCaseProvider) {
    this.getExchangeListUseCaseProvider = getExchangeListUseCaseProvider;
  }

  @Override
  public ExchangeListViewModel get() {
    return newInstance(getExchangeListUseCaseProvider.get());
  }

  public static ExchangeListViewModel_Factory create(
      Provider<GetExchangeListUseCase> getExchangeListUseCaseProvider) {
    return new ExchangeListViewModel_Factory(getExchangeListUseCaseProvider);
  }

  public static ExchangeListViewModel newInstance(GetExchangeListUseCase getExchangeListUseCase) {
    return new ExchangeListViewModel(getExchangeListUseCase);
  }
}
