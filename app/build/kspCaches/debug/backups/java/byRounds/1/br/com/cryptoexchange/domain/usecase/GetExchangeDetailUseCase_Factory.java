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
public final class GetExchangeDetailUseCase_Factory implements Factory<GetExchangeDetailUseCase> {
  private final Provider<ExchangeRepository> repositoryProvider;

  public GetExchangeDetailUseCase_Factory(Provider<ExchangeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetExchangeDetailUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetExchangeDetailUseCase_Factory create(
      Provider<ExchangeRepository> repositoryProvider) {
    return new GetExchangeDetailUseCase_Factory(repositoryProvider);
  }

  public static GetExchangeDetailUseCase newInstance(ExchangeRepository repository) {
    return new GetExchangeDetailUseCase(repository);
  }
}
