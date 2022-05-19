export function isPackageInstalledAsync(packageName: string): Promise<boolean>;

export function startAsync<TResult>(
  packageName: string,
  className: string,
  action: string,
  params: object
): Promise<TResult>;
