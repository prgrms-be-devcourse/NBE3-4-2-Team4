export function convertSnakeToCamel<T>(obj: T): T {
    if (Array.isArray(obj)) {
      return obj.map((item) => convertSnakeToCamel(item)) as T;
    } else if (typeof obj === "object" && obj !== null) {
      return Object.fromEntries(
        Object.entries(obj).map(([key, value]) => [
          key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase()),
          convertSnakeToCamel(value),
        ])
      ) as T;
    }
    return obj;
  }