export class Page<T> {

  entities: Array<T> = [];
  totalRecords: number;
  filteredEntities: number;
  first: number;
  pageSize: number;
  offset: number;
  filters: any;
}

export class DataFilter {
  matchMode: string;
  value: any;
}

export class BodyRequest {
    sortBy: string;
    pageSize: number;
    sortAsc: boolean;
    offset: number;
    filters: { [name: string]: any };
    //getFilterOptions: boolean;
  }
