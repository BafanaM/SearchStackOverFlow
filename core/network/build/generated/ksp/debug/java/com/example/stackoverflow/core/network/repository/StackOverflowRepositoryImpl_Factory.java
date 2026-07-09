package com.example.stackoverflow.core.network.repository;

import com.example.stackoverflow.core.network.remote.StackExchangeApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class StackOverflowRepositoryImpl_Factory implements Factory<StackOverflowRepositoryImpl> {
  private final Provider<StackExchangeApi> apiProvider;

  private StackOverflowRepositoryImpl_Factory(Provider<StackExchangeApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public StackOverflowRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static StackOverflowRepositoryImpl_Factory create(Provider<StackExchangeApi> apiProvider) {
    return new StackOverflowRepositoryImpl_Factory(apiProvider);
  }

  public static StackOverflowRepositoryImpl newInstance(StackExchangeApi api) {
    return new StackOverflowRepositoryImpl(api);
  }
}
