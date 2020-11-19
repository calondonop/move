import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from './../../../environments/environment';
import { Move } from './../model/move';


@Injectable({
  providedIn: 'root'
})
export class MoveService {

  constructor(
    private http: HttpClient
  ) { }

  postFile(move: Move): Observable<any> {
    const fileData: FormData = new FormData();
    fileData.append('file', move.file, move.file.name);
    fileData.append('id', move.id);
    return this.http
    .post(environment.apiUrl.move, fileData, {responseType: 'arraybuffer'}).pipe(
      map((fileResponse: any) => new Blob([fileResponse], { type: 'txt' }))
    );
  }

}
