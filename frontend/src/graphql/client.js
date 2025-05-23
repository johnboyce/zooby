const API_URL = "http://localhost:8080/graphql";

export async function fetchGraphQL(query, variables = {}) {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ query, variables }),
  });

  const result = await response.json();
  if (result.errors) {
    console.error("GraphQL error:", result.errors);
    throw new Error(result.errors[0].message);
  }

  return result.data;
}
