package br.com.cryptoexchange.ui.screens.exchangedetail;

import androidx.lifecycle.SavedStateHandle;
import br.com.cryptoexchange.domain.usecase.GetExchangeDetailUseCase;
import br.com.cryptoexchange.domain.usecase.GetExchangeMarketPairsUseCase;
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
public final class ExchangeDetailViewModel_Factory implements Factory<ExchangeDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<GetExchangeDetailUseCase> getExchangeDetailUseCaseProvider;

  private final Provider<GetExchangeMarketPairsUseCase> getExchangeMarketPairsUseCaseProvider;

  public ExchangeDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetExchangeDetailUseCase> getExchangeDetailUseCaseProvider,
      Provider<GetExchangeMarketPairsUseCase> getExchangeMarketPairsUseCaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.getExchangeDetailUseCaseProvider = getExchangeDetailUseCaseProvider;
    this.getExchangeMarketPairsUseCaseProvider = getExchangeMarketPairsUseCaseProvider;
  }

  @Override
  public ExchangeDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), getExchangeDetailUseCaseProvider.get(), getExchangeMarketPairsUseCaseProvider.get());
  }

  public static ExchangeDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetExchangeDetailUseCase> getExchangeDetailUseCaseProvider,
      Provider<GetExchangeMarketPairsUseCase> getExchangeMarketPairsUseCaseProvider) {
    return new ExchangeDetailViewModel_Factory(savedStateHandleProvider, getExchangeDetailUseCaseProvider, getExchangeMarketPairsUseCaseProvider);
  }

  public static ExchangeDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      GetExchangeDetailUseCase getExchangeDetailUseCase,
      GetExchangeMarketPairsUseCase getExchangeMarketPairsUseCase) {
    return new ExchangeDetailViewModel(savedStateHandle, getExchangeDetailUseCase, getExchangeMarketPairsUseCase);
  }
}
