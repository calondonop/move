import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MoveService } from '../shared/service/move.service';

@Component({
  selector: 'app-move',
  templateUrl: './move.component.html',
  styleUrls: ['./move.component.css']
})
export class MoveComponent implements OnInit {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private moveService: MoveService
  ) { }

  ngOnInit(): void {
    this.createForm();
  }

  createForm(): void {
    this.form = this.fb.group({
      id: ['', [Validators.required, Validators.pattern('[0-9]*')]],
      file: ['', [Validators.required]]
    });
  }

  handleFileInput(files: FileList): void {
    const fileToUpload: File = files?.item(0);
    this.form.controls.file.setValue(fileToUpload);
  }

  saveAs(blob: any, name: string): void {
    const link = document.createElement('a');
    link.setAttribute('id', 'linkDownload');
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', name);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }

  uploadFileToActivity(): void {
    if (!this.form.valid) { return; }
    this.moveService.postFile(this.form.getRawValue()).subscribe(
      (fileBlob: Blob) => this.saveAs(fileBlob, 'moves.txt'),
      error => {
      console.log(error);
    });
  }
}
