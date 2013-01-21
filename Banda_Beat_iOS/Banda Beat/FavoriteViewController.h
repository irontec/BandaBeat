//
//  FavoriteViewController.h
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import "GAITrackedViewController.h"

@class InformationView;

@interface FavoriteViewController : GAITrackedViewController
@property(weak, nonatomic) IBOutlet UITableView *tableView;
@property(weak, nonatomic) IBOutlet UIBarButtonItem *editButton;
@property(strong,nonatomic) NSMutableArray *tracks;
@property(strong, nonatomic) InformationView *infoView;

-(IBAction)editMode:(id)sender;
@end
