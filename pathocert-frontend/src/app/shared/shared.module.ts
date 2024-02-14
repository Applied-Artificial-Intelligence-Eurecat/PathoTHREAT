import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TableModule } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { ToastModule } from 'primeng/toast';
import { DropdownModule } from 'primeng/dropdown';
import { PickListModule } from 'primeng/picklist';
import {ProgressSpinnerModule} from 'primeng/progressspinner';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { FieldsetModule } from 'primeng/fieldset';
import { ChipsModule } from 'primeng/chips';
import { TabViewModule } from 'primeng/tabview';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { DialogModule } from 'primeng/dialog';
import { MessageModule } from 'primeng/message';
import { MessagesModule } from 'primeng/messages';
import { ContextMenuModule } from 'primeng/contextmenu';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, TreeNode, MessageService } from 'primeng/api';
import { TreeModule } from 'primeng/tree';
import { TreeDragDropService } from 'primeng/api';
import { InputSwitchModule } from 'primeng/inputswitch';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TooltipModule } from 'primeng/tooltip';
import { PanelMenuModule } from 'primeng/panelmenu';
import { MenuModule } from 'primeng/menu';
import { MenubarModule } from 'primeng/menubar';
import { TieredMenuModule } from 'primeng/tieredmenu';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { CardModule } from 'primeng/card';
import { AccordionModule } from 'primeng/accordion';
import { EditorModule } from 'primeng/editor';
import { TagModule } from 'primeng/tag';

import { DropDownValuesService } from 'src/app/services/dropdown-values.service';
import { EmergencyService } from 'src/app/services/emergency.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ToolbarModule,
    PanelMenuModule,
    MenuModule,
    MenubarModule,
    TieredMenuModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    CheckboxModule,
    MessagesModule,
    MessageModule,
    DialogModule,
    ScrollPanelModule,
    TabViewModule,
    DropdownModule,
    PickListModule,
    ProgressSpinnerModule,
    BreadcrumbModule,
    FieldsetModule,
    ChipsModule,
    ToastModule,
    TreeModule,
    ContextMenuModule,
    InputSwitchModule,
    InputTextareaModule,
    TooltipModule,
    OverlayPanelModule,
    CardModule,
    AccordionModule,
    EditorModule,
    TagModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ToolbarModule,
    PanelMenuModule,
    MenuModule,
    MenubarModule,
    TieredMenuModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    CheckboxModule,
    MessagesModule,
    MessageModule,
    DialogModule,
    ConfirmDialogModule,
    ScrollPanelModule,
    TabViewModule,
    DropdownModule,
    PickListModule,
    ProgressSpinnerModule,
    BreadcrumbModule,
    FieldsetModule,
    ChipsModule,
    ToastModule,
    TreeModule,
    InputSwitchModule,
    InputTextareaModule,
    TooltipModule,
    OverlayPanelModule,
    CardModule,
    AccordionModule,
    EditorModule,
    TagModule
  ],
  providers: [
    ConfirmationService,
    TreeDragDropService,
    MessageService,
    DropDownValuesService,
    EmergencyService
  ]
})
export class SharedModule { }
