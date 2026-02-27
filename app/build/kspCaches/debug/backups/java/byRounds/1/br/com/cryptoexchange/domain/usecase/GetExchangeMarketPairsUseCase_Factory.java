package br.com.cryptoexchange.domain.usecase;

import br.com.cryptoexchange.domain.repository.ExchangeRepository;
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
public final class GetExchangeMarketPairsUseCase_Factory implements Factory<GetExchangeMarketPairsUseCase> {
  private final Provider<ExchangeRepository> repositoryProvider;

  public GetExchangeMarketPairsUseCase_Factory(Provider<ExchangeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetExchangeMarketPairsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetExchangeMarketPairsUseCase_Factory create(
      Provider<ExchangeRepository> repositoryProvider) {
    return new GetExchangeMarketPairsUseCase_Factory(repositoryProvider);
  }

  public static GetExchangeMarketPairsUseCase newInstance(ExchangeRepository repository) {
    return new GetExchangeMarketPairsUseCase(repository);
  }
}
